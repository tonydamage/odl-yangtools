/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.impl.schema.tree;

import com.google.common.base.Optional;

import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.PathArgument;
import org.opendaylight.yangtools.yang.data.api.schema.tree.DataValidationFailedException;
import org.opendaylight.yangtools.yang.data.api.schema.tree.spi.TreeNode;
import org.opendaylight.yangtools.yang.data.api.schema.tree.spi.Version;

/**
 * Represents a {@link ModificationApplyOperation} which is rooted at conceptual
 * top of data tree.
 *
 * <p>
 * This implementation differs from other implementations in this package that
 * is not immutable, but may be upgraded to newer state if available by
 * explicitly invoking {@link #upgradeIfPossible()} and also serves as factory
 * for deriving snapshot {@link RootModificationApplyOperation} which will not
 * be affected by upgrade of original one.
 *
 * <p>
 * There are two variations of this {@link ModificationApplyOperation}:
 * <ul>
 * <li>
 * <b>Upgradable</b> - operation may be upgraded to different backing
 * implementation by invoking {@link #upgradeIfPossible()}.</li>
 * <li><b>Not Upgradable</b> - operation is immutable, invocation of
 * {@link #upgradeIfPossible()} is no-op and method {@link #snapshot()} returns
 * pointer on same object.
 *
 * <h3>Upgradable Root Modification Operation</h3>
 *
 * Upgradable Root Modification Operation may be created using:
 * <ul>
 * <li> {@link #from(ModificationApplyOperation)} with other upgradable root
 * modification as an argument
 * <li>using factory {@link LatestOperationHolder} which instantiates Upgradable
 * Root Modification Operations and provides an option to set latest
 * implementation.
 * </ul>
 * <p>
 * Upgradable root operation is never upgraded to latest operation
 * automatically, but client code must explicitly invoke
 * {@link #upgradeIfPossible()} to get latest implementation.
 *
 * Note: This is helpful for implementing
 * {@link org.opendaylight.yangtools.yang.data.api.schema.tree.DataTreeModification}
 * which may be derived from
 * {@link org.opendaylight.yangtools.yang.data.api.schema.tree.DataTree} before
 * update of schema and user actually writes data after schema update. During
 * update user did not invoked any operation.
 *
 */
abstract class RootModificationApplyOperation implements ModificationApplyOperation {

    @Override
    public Optional<ModificationApplyOperation> getChild(final PathArgument child) {
        return getDelegate().getChild(child);
    }

    @Override
    public final void checkApplicable(final YangInstanceIdentifier path, final NodeModification modification, final Optional<TreeNode> current)
            throws DataValidationFailedException {
        getDelegate().checkApplicable(path, modification, current);
    }

    @Override
    public final Optional<TreeNode> apply(final ModifiedNode modification, final Optional<TreeNode> currentMeta,
            final Version version) {
        return getDelegate().apply(modification, currentMeta, version);
    }

    @Override
    public boolean equals(final Object obj) {
        return getDelegate().equals(obj);
    }

    @Override
    public int hashCode() {
        return getDelegate().hashCode();
    }

    @Override
    public String toString() {
        return getDelegate().toString();
    }

    @Override
    public void verifyStructure(final ModifiedNode modification) throws IllegalArgumentException {
        getDelegate().verifyStructure(modification);
    }

    /**
     * Return the underlying delegate.
     *
     * @return Underlying delegate.
     */
    abstract ModificationApplyOperation getDelegate();

    /**
     * Creates a snapshot from this modification, which may have separate
     * upgrade lifecycle and is not affected by upgrades
     * <p>
     * Newly created snapshot uses backing implementation of this modi
     *
     * @return Derived {@link RootModificationApplyOperation} with separate
     *         upgrade lifecycle.
     */
    public abstract RootModificationApplyOperation snapshot();

    /**
     * Upgrades backing implementation to latest available, if possible.
     * <p>
     * Latest implementation of {@link RootModificationApplyOperation} is
     * managed by {@link LatestOperationHolder} which was used to construct this
     * operation and latest operation is updated by
     * {@link LatestOperationHolder#setCurrent(ModificationApplyOperation)}.
     */
    public abstract void upgradeIfPossible();

    public static RootModificationApplyOperation from(final ModificationApplyOperation resolver) {
        if (resolver instanceof RootModificationApplyOperation) {
            return ((RootModificationApplyOperation) resolver).snapshot();
        }
        return new NotUpgradable(resolver);
    }

    /**
     * Implementation of Upgradable {@link RootModificationApplyOperation}
     *
     * This implementation is associated with {@link LatestOperationHolder}
     * which holds latest available implementation, which may be used for
     * upgrade.
     *
     * Upgrading {@link LatestOperationHolder} will not affect any instance,
     * unless client invoked {@link #upgradeIfPossible()} which will result in
     * changing delegate to the latest one.
     *
     */
    private static final class Upgradable extends RootModificationApplyOperation {

        private final LatestOperationHolder holder;
        private ModificationApplyOperation delegate;

        public Upgradable(final LatestOperationHolder holder, final ModificationApplyOperation delegate) {
            this.holder = holder;
            this.delegate = delegate;

        }

        @Override
        public void upgradeIfPossible() {
            ModificationApplyOperation holderCurrent = holder.getCurrent();
            if (holderCurrent != delegate) {
                // FIXME: Allow update only if there is addition of models, not
                // removals.
                delegate = holderCurrent;
            }

        }

        @Override
        ModificationApplyOperation getDelegate() {
            return delegate;
        }

        @Override
        public RootModificationApplyOperation snapshot() {
            return new Upgradable(holder, getDelegate());
        }

    }

    private static final class NotUpgradable extends RootModificationApplyOperation {

        private final ModificationApplyOperation delegate;

        public NotUpgradable(final ModificationApplyOperation delegate) {
            this.delegate = delegate;
        }

        @Override
        public ModificationApplyOperation getDelegate() {
            return delegate;
        }

        @Override
        public void upgradeIfPossible() {
            // Intentional noop
        }

        @Override
        public RootModificationApplyOperation snapshot() {
            return this;
        }
    }

    /**
     * Holder and factory for upgradable root modifications
     *
     * This class is factory for upgradable root modifications and provides an
     * access to set latest backing implementation.
     *
     */
    static class LatestOperationHolder {

        private ModificationApplyOperation current = new AlwaysFailOperation();

        /**
         * Return latest backing implemenation
         *
         * @return
         */
        public ModificationApplyOperation getCurrent() {
            return current;
        }

        /**
         * Sets latest backing implementation of associated
         * {@link RootModificationApplyOperation}.
         * <p>
         * Note: This does not result in upgrading implementation of already
         * existing {@link RootModificationApplyOperation}. Users, which
         * obtained instances using {@link #newSnapshot()}, deriving
         * {@link RootModificationApplyOperation} from this modification must
         * explicitly invoke
         * {@link RootModificationApplyOperation#upgradeIfPossible()} on their
         * instance to be updated to latest backing implementation.
         *
         * @param newApplyOper
         *            New backing implementation
         */
        public void setCurrent(final ModificationApplyOperation newApplyOper) {
            current = newApplyOper;
        }

        /**
         *
         * Creates new upgradable {@link RootModificationApplyOperation}
         * associated with holder.
         *
         * @return New upgradable {@link RootModificationApplyOperation} with
         *         {@link #getCurrent()} used as backing implementation.
         */
        public RootModificationApplyOperation newSnapshot() {
            return new Upgradable(this, current);
        }

    }
}
