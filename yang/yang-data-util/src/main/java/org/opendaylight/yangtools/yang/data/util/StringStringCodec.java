/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import org.opendaylight.yangtools.yang.data.api.codec.StringCodec;
import org.opendaylight.yangtools.yang.model.api.type.StringTypeDefinition;

final class StringStringCodec extends LeafSimpleValueStringCodec<String, StringTypeDefinition> implements
        StringCodec<String> {

    protected StringStringCodec(final StringTypeDefinition typeDef) {
        super(typeDef, String.class);
    }

    @Override
    public String deserialize(final String stringRepresentation) {
        return stringRepresentation == null ? "" : stringRepresentation;
    }

    @Override
    public String serialize(final String data) {
        return data == null ? "" : data;
    }
}