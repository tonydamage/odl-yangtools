/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.api.codec;

import org.opendaylight.yangtools.concepts.Codec;

/**
 *
 * Value codec for Yang Modeled data.
 *
 * Note do not or use this implement interface directly, use {@link ContextAgnosticCodec}
 * or {@link ContextSensitiveCodec} codecs instead.
 *
 *
 * @param <P> Result of serialization
 * @param <I> Deserialized representation
 * @param <C> Context type
 */
interface ValueCodec<P,I,C extends ValueCodec<?,?,C>>  extends Codec<P, I> {

}
