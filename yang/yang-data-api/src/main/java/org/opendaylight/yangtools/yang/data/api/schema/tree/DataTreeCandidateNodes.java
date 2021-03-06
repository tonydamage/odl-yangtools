/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.api.schema.tree;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;

@Beta
public final class DataTreeCandidateNodes {
    private DataTreeCandidateNodes() {
        throw new UnsupportedOperationException();
    }

    public static DataTreeCandidateNode fromNormalizedNode(final NormalizedNode<?, ?> node) {
        return new NormalizedNodeDataTreeCandidateNode(node);
    }

    public static void applyToCursor(final DataTreeModificationCursor cursor, final DataTreeCandidateNode node) {
        switch (node.getModificationType()) {
        case DELETE:
            cursor.delete(node.getIdentifier());
            break;
        case SUBTREE_MODIFIED:
                cursor.enter(node.getIdentifier());
                AbstractNodeIterator iterator = new ExitingNodeIterator(null, node.getChildNodes().iterator());
            do {
                iterator = iterator.next(cursor);
            } while (iterator != null);
            break;
        case UNMODIFIED:
            // No-op
            break;
        case WRITE:
            cursor.write(node.getIdentifier(), node.getDataAfter().get());
            break;
        default:
            throw new IllegalArgumentException("Unsupported modification " + node.getModificationType());
        }
    }

    public static void applyRootToCursor(final DataTreeModificationCursor cursor, final DataTreeCandidateNode node) {
        switch (node.getModificationType()) {
            case DELETE:
                throw new IllegalArgumentException("Can not delete root.");
            case WRITE:
            case SUBTREE_MODIFIED:
                AbstractNodeIterator iterator = new RootNonExitingIterator(node.getChildNodes().iterator());
                do {
                    iterator = iterator.next(cursor);
                } while (iterator != null);
                break;
            case UNMODIFIED:
                // No-op
                break;
            default:
                throw new IllegalArgumentException("Unsupported modification " + node.getModificationType());
        }
    }

    private abstract static class AbstractNodeIterator {
        private final Iterator<DataTreeCandidateNode> iterator;

        AbstractNodeIterator(final Iterator<DataTreeCandidateNode> iterator) {
            this.iterator = Preconditions.checkNotNull(iterator);
        }

        AbstractNodeIterator next(final DataTreeModificationCursor cursor) {
            while (iterator.hasNext()) {
                final DataTreeCandidateNode node = iterator.next();
                switch (node.getModificationType()) {
                case DELETE:
                    cursor.delete(node.getIdentifier());
                    break;
                case APPEARED:
                case DISAPPEARED:
                case SUBTREE_MODIFIED:
                    final Collection<DataTreeCandidateNode> children = node.getChildNodes();
                    if (!children.isEmpty()) {
                        cursor.enter(node.getIdentifier());
                            return new ExitingNodeIterator(this, children.iterator());
                    }
                    break;
                case UNMODIFIED:
                    // No-op
                    break;
                case WRITE:
                    cursor.write(node.getIdentifier(), node.getDataAfter().get());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported modification " + node.getModificationType());
                }
            }
            exitNode(cursor);
            return getParent();
        }

        protected abstract @Nullable AbstractNodeIterator getParent();

        protected abstract void exitNode(DataTreeModificationCursor cursor);
    }

    private static final class RootNonExitingIterator extends AbstractNodeIterator {

        protected RootNonExitingIterator(@Nonnull final Iterator<DataTreeCandidateNode> iterator) {
            super(iterator);
        }

        @Override
        protected void exitNode(final DataTreeModificationCursor cursor) {
            // Intentional noop.
        }

        @Override
        protected AbstractNodeIterator getParent() {
            return null;
        }
    }

    private static final class ExitingNodeIterator extends AbstractNodeIterator {

        private final AbstractNodeIterator parent;

        public ExitingNodeIterator(@Nullable final AbstractNodeIterator parent,
                @Nonnull final Iterator<DataTreeCandidateNode> iterator) {
            super(iterator);
            this.parent = parent;
        }

        @Override
        protected AbstractNodeIterator getParent() {
            return parent;
        }

        @Override
        protected void exitNode(final DataTreeModificationCursor cursor) {
            cursor.exit();
        }
    }
}
