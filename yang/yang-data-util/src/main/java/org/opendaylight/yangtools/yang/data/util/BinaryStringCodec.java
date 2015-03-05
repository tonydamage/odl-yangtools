/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import com.google.common.io.BaseEncoding;
import javax.xml.bind.DatatypeConverter;
import org.opendaylight.yangtools.yang.data.api.codec.BinaryCodec;
import org.opendaylight.yangtools.yang.model.api.type.BinaryTypeDefinition;

final class BinaryStringCodec extends LeafSimpleValueStringCodec<byte[], BinaryTypeDefinition>
        implements BinaryCodec<String> {

    protected BinaryStringCodec(final BinaryTypeDefinition typeDef) {
        super(typeDef, byte[].class);
    }

    @Override
    public String serialize(final byte[] data) {
        return data == null ? "" : BaseEncoding.base64().encode(data);
    }

    @Override
    public byte[] deserialize(final String stringRepresentation) {
        return stringRepresentation == null ? null : DatatypeConverter.parseBase64Binary(stringRepresentation);
    }
}