/*
 * Copyright (c) 2001-2016 Caucho Technology, Inc.  All rights reserved.
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * @author Scott Ferguson
 */

package com.caucho.v5.h3.ser;

import com.caucho.v5.h3.OutH3;
import com.caucho.v5.h3.context.ContextH3;
import com.caucho.v5.h3.io.ClassInfoH3;
import com.caucho.v5.h3.io.ConstH3;
import com.caucho.v5.h3.io.InH3Amp;
import com.caucho.v5.h3.io.InRawH3;
import com.caucho.v5.h3.io.OutRawH3;
import com.caucho.v5.h3.query.PathH3Amp;
import com.caucho.v5.util.L10N;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * H3 typed object array serializer.
 */
public class SerializerH3ArrayObject extends SerializerH3Base<Object[]>
{
  private static final L10N L = new L10N(SerializerH3ArrayObject.class);

  private AtomicReference<ClassInfoH3> _infoRef = new AtomicReference<>();

  private MethodHandle _ctor;

  SerializerH3ArrayObject()
  {
  }

  @Override
  public Type type()
  {
    return Object[].class;
  }

  @Override
  public int typeSequence()
  {
    //return ConstH3.DEF_ARRAY_OBJECT;
    return ConstH3.DEF_OBJECT_ARRAY;
  }

  @Override
  public void writeObject(OutRawH3 os, int defIndex, Object[] list, OutH3 out)
  {
    os.writeObject(defIndex);

    int size = list.length;

    os.writeChunk(size, true);

    for (Object entry : list) {
      out.writeObject(entry);
      ;
    }
  }

  /**
   * Introspect the class.
   */
  @Override
  public void introspect(ContextH3 context)
  {
  }

  @Override
  public Object[] readObject(InRawH3 is, InH3Amp in)
  {
    while (true) {
      long chunk = is.readUnsigned();
      int size = (int) InRawH3.chunkSize(chunk);

      Object[] array = new Object[size];

      for (int i = 0; i < size; i++) {
        Object item = in.readObject();
        array[i] = item;
      }

      if (InRawH3.chunkIsFinal(chunk)) {
        return array;
      }
      else {
        throw new UnsupportedOperationException(getClass().getName());
      }
    }
  }

  @Override
  public void scan(InRawH3 is, PathH3Amp path, InH3Amp in, Object[] values)
  {
    while (true) {
      long chunk = is.readUnsigned();
      long size = InRawH3.chunkSize(chunk);

      for (int i = 0; i < size; i++) {
        is.skip(in);
      }

      if (InRawH3.chunkIsFinal(chunk)) {
        return;
      }
    }
  }

  @Override
  public void skip(InRawH3 is, InH3Amp in)
  {
    while (true) {
      long chunk = is.readUnsigned();
      long size = InRawH3.chunkSize(chunk);

      for (int i = 0; i < size; i++) {
        is.skip(in);
      }

      if (InRawH3.chunkIsFinal(chunk)) {
        return;
      }
    }
  }
}
