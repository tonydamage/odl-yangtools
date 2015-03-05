package org.opendaylight.yangtools.yang.model.util.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 *
 * Abstract base cursor, which allows walking down a child axis based on provided
 *
 *
 */
@NotThreadSafe
public abstract class AbstractTreeCursor<I,N> {

    private N current;

    public AbstractTreeCursor(final N current) {
        this.current = current;
    }

    public final boolean enter(final I node) {
       if(current != null) {
           final N previous = current;
           setCurrent(getChild(current,node));
           if(current != null) {
               childEntered(previous,current);
           }
           return current != null;

       }
       return false;
    }

    protected final void setCurrent(final N current) {
        this.current = current;
    }

    protected void childEntered(@Nonnull final N previous, final N current) {
        // Default NOOP implementation
    }

    protected void checkIdentifier(final I identifier) {
        // Default implementation is NOOP implementation
     }

    protected abstract @Nullable N getChild(final N current, final I identifier);

    public final N getCurrent() {
        return current;
    }

}
