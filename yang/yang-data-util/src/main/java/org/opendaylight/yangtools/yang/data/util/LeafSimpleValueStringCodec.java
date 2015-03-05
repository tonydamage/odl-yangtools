/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import static org.opendaylight.yangtools.yang.model.util.BaseTypes.INT16_QNAME;
import static org.opendaylight.yangtools.yang.model.util.BaseTypes.INT32_QNAME;
import static org.opendaylight.yangtools.yang.model.util.BaseTypes.INT64_QNAME;
import static org.opendaylight.yangtools.yang.model.util.BaseTypes.INT8_QNAME;
import static org.opendaylight.yangtools.yang.model.util.BaseTypes.UINT16_QNAME;
import static org.opendaylight.yangtools.yang.model.util.BaseTypes.UINT32_QNAME;
import static org.opendaylight.yangtools.yang.model.util.BaseTypes.UINT64_QNAME;
import static org.opendaylight.yangtools.yang.model.util.BaseTypes.UINT8_QNAME;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import java.util.regex.Pattern;
import org.opendaylight.yangtools.yang.data.api.codec.ContextAgnosticCodec;
import org.opendaylight.yangtools.yang.model.api.TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.BinaryTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.BitsTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.BooleanTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.DecimalTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.EmptyTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.EnumTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.IntegerTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.StringTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.UnsignedIntegerTypeDefinition;
import org.opendaylight.yangtools.yang.model.util.DerivedType;
import org.opendaylight.yangtools.yang.model.util.ExtendedType;

public abstract class LeafSimpleValueStringCodec<J, T extends TypeDefinition<T>> implements ContextAgnosticCodec<String,J> {

    private static final Pattern intPattern = Pattern.compile("[+-]?[1-9][0-9]*$");
    private static final Pattern hexPattern = Pattern.compile("[+-]?0[xX][0-9a-fA-F]+");
    private static final Pattern octalPattern = Pattern.compile("[+-]?0[1-7][0-7]*$");

    // For up to two characters, this is very fast
    private static final CharMatcher X_MATCHER = CharMatcher.anyOf("xX");

    private final T typeDefinition;
    private final Class<J> inputClass;

    static final int provideBase(final String integer) {
        if (integer == null) {
            throw new IllegalArgumentException("String representing integer number cannot be NULL");
        }

        if ((integer.length() == 1) && (integer.charAt(0) == '0')) {
            return 10;
        }

        if (intPattern.matcher(integer).matches()) {
            return 10;
        }
        if (hexPattern.matcher(integer).matches()) {
                return 16;
        }
        if (octalPattern.matcher(integer).matches()) {
            return 8;
        }
        String formatedMessage = String.format("Incorrect lexical representation of integer value: %s."
                + "%nAn integer value can be defined as: "
                + "%n  - a decimal number,"
                + "%n  - a hexadecimal number (prefix 0x),"
                + "%n  - an octal number (prefix 0)."
                + "%nSigned values are allowed. Spaces between digits are NOT allowed.", integer);
        throw new NumberFormatException(formatedMessage);
    }

    static String normalizeHexadecimal(final String hexInt) {
        if (hexInt == null) {
            throw new IllegalArgumentException(
                    "String representing integer number in Hexadecimal format cannot be NULL!");
        }

        return X_MATCHER.removeFrom(hexInt);
    }

    public Class<J> getInputClass() {
        return inputClass;
    }

    LeafSimpleValueStringCodec(final T typeDefinition, final Class<J> outputClass) {
        Preconditions.checkArgument(outputClass != null, "Output class must be specified.");
        this.typeDefinition = typeDefinition;
        this.inputClass = outputClass;
    }

    public T getTypeDefinition() {
        return typeDefinition;
    }

    public static final LeafSimpleValueStringCodec<?, ?> from(final TypeDefinition<?> typeDefinition) {

        final TypeDefinition<?> type;
        if(typeDefinition instanceof ExtendedType) {
            type = DerivedType.from((ExtendedType)typeDefinition);
        } else {
            type = typeDefinition;
        }
        TypeDefinition<?> superType = type;
        while (superType.getBaseType() != null) {
            superType = superType.getBaseType();
        }
        if (type instanceof BinaryTypeDefinition) {
            return new BinaryStringCodec((BinaryTypeDefinition) type);
        }
        if (type instanceof BitsTypeDefinition) {
            return new BitsStringCodec(((BitsTypeDefinition) type));
        }
        if (type instanceof BooleanTypeDefinition) {
            return new BooleanStringCodec((BooleanTypeDefinition) type);
        }
        if (type instanceof DecimalTypeDefinition) {
            return new DecimalStringCodec((DecimalTypeDefinition) type);
        }
        if (type instanceof EmptyTypeDefinition) {
            return new EmptyStringCodec((EmptyTypeDefinition) type);
        }
        if (type instanceof EnumTypeDefinition) {
            return new EnumStringCodec(((EnumTypeDefinition) type));
        }
        if (type instanceof IntegerTypeDefinition) {
            if (INT8_QNAME.equals(superType.getQName())) {
                return new Int8StringCodec((IntegerTypeDefinition) type);
            }
            if (INT16_QNAME.equals(superType.getQName())) {
                return new Int16StringCodec((IntegerTypeDefinition) type);
            }
            if (INT32_QNAME.equals(superType.getQName())) {
                return new Int32StringCodec((IntegerTypeDefinition) type);
            }
            if (INT64_QNAME.equals(superType.getQName())) {
                return new Int64StringCodec((IntegerTypeDefinition) type);
            }
        }
        if (type instanceof StringTypeDefinition) {
            return new StringStringCodec((StringTypeDefinition) type);
        }
        if (type instanceof UnsignedIntegerTypeDefinition) {
            if (UINT8_QNAME.equals(superType.getQName())) {
                return new Uint8StringCodec((UnsignedIntegerTypeDefinition) type);
            }
            if (UINT16_QNAME.equals(superType.getQName())) {
                return new Uint16StringCodec((UnsignedIntegerTypeDefinition) type);
            }
            if (UINT32_QNAME.equals(superType.getQName())) {
                return new Uint32StringCodec((UnsignedIntegerTypeDefinition) type);
            }
            if (UINT64_QNAME.equals(superType.getQName())) {
                return new Uint64StringCodec((UnsignedIntegerTypeDefinition) type);
            }
        }
        return null;
    };
}
