/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import com.google.common.base.Preconditions;
import java.math.BigDecimal;
import org.opendaylight.yangtools.yang.data.api.codec.DecimalCodec;
import org.opendaylight.yangtools.yang.model.api.type.DecimalTypeDefinition;

final class DecimalStringCodec extends LeafSimpleValueStringCodec<BigDecimal, DecimalTypeDefinition>
        implements DecimalCodec<String> {

    protected DecimalStringCodec(final DecimalTypeDefinition typeDef) {
        super(typeDef, BigDecimal.class);
    }

    @Override
    public String serialize(final BigDecimal data) {
        return data == null ? "" : data.toString();
    }

    @Override
    public BigDecimal deserialize(final String stringRepresentation) {
        Preconditions.checkArgument( stringRepresentation != null , "Input cannot be null" );
        return new BigDecimal(stringRepresentation);
    }
}