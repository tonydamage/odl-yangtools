package org.opendaylight.yangtools.yang.model.util.tree;

import javax.annotation.Nullable;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.DataNodeContainer;
import org.opendaylight.yangtools.yang.model.api.NotificationDefinition;
import org.opendaylight.yangtools.yang.model.api.RpcDefinition;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import org.opendaylight.yangtools.yang.model.api.SchemaNode;

public class SchemaTreeCursor extends AbstractTreeCursor<QName, SchemaNode> {

    protected SchemaTreeCursor(final SchemaNode current) {
        super(current);
    }

    @Override
    public final SchemaNode getChild(final SchemaNode current,final QName name) {
        if(current instanceof SchemaContext) {
            return getSchemaTreeChild((SchemaContext) current,name);
        }
        if(current instanceof RpcDefinition) {
            return getSchemaTreeChild((RpcDefinition) current, name);
        }
        if(current instanceof DataNodeContainer) {
            return ((DataNodeContainer) current).getDataChildByName(name);
        }
        return null;
    }


    private static @Nullable SchemaNode getSchemaTreeChild(final RpcDefinition current, final QName name) {
        if(current.getQName().getModule().equals(name.getModule())) {
            if("input".equals(name.getLocalName())) {
                return current.getInput();
            } else if("output".equals(name.getLocalName())) {
                return current.getOutput();
            }
        }
        return null;
    }

    private static @Nullable SchemaNode getSchemaTreeChild(final SchemaContext current, final QName name) {
        SchemaNode potential = current.getDataChildByName(name);
        if(potential != null) {
            return potential;
        }
        potential = getRpcDefinition(current,name);
        if(potential != null) {
            return potential;
        }
        potential = getNotification(current,name);
        return potential;
    }

    private static @Nullable SchemaNode getRpcDefinition(final SchemaContext current, final QName name) {
        for(final RpcDefinition def : current.getOperations()) {
            if(name.equals(def.getQName())) {
                return def;
            }
        }
        return null;
    }

    private static SchemaNode getNotification(final SchemaContext current, final QName name) {
        for(final NotificationDefinition def : current.getNotifications()) {
            if(name.equals(def.getQName())) {
                return def;
            }
        }
        return null;
    }

}