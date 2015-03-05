package org.opendaylight.yangtools.yang.model.util.tree;

import java.util.ArrayDeque;
import java.util.Collection;
import org.opendaylight.yangtools.yang.model.api.SchemaNode;

public class ParentSchemaTreeCursor extends SchemaTreeCursor {

    ArrayDeque<SchemaNode> ancestors;

    protected ParentSchemaTreeCursor(final SchemaNode current,final Collection<SchemaNode> node) {
        super(current);
        this.ancestors = new ArrayDeque<>(ancestors);
    }

    @Override
    protected void childEntered(final SchemaNode previous, final SchemaNode current) {
        ancestors.push(previous);
    }

    public boolean enterParent() {
        // FIXME: Add proper error handling
        final SchemaNode parent = ancestors.pop();
        setCurrent(parent);
        return true;
    }

}
