/*
 * Copyright 2015 Otto (GmbH & Co KG)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flinkspector.datastream

import io.flinkspector.CoreSpec
import io.flinkspector.core.collection.ExpectedRecords
import org.apache.flink.api.common.functions.FilterFunction
import org.apache.flink.streaming.api.datastream.DataStream

import scala.collection.JavaConversions._

class StreamTestBaseSpec extends CoreSpec {

  it should "run a basic test" in {
    val base = new DataStreamTestBase
    base.initialize()

    val coll: java.util.Collection[Int] = List(1, 2, 3, 4)
    val stream: DataStream[Int] = base.createTestStream(coll)
    val matcher = ExpectedRecords.create(coll)

    base.assertStream(stream, matcher)
    base.executeTest()
  }

  it should "fail a basic test" in {
    val base = new DataStreamTestBase
    base.initialize()

    val coll: java.util.Collection[Int] = List(1, 2, 3, 4)
    val stream: DataStream[Int] = base.createTestStream(List(1, 2, 3))
    val matcher = ExpectedRecords.create(coll)

    base.assertStream(stream, matcher)
    an[AssertionError] shouldBe thrownBy(base.executeTest())
  }

  it should "fail a test without output" in {
    val base = new DataStreamTestBase
    base.initialize()

    val coll: java.util.Collection[Int] = List(1, 2, 3, 4)
    val stream: DataStream[Int] = base.createTestStream(List(1))
    val matcher = ExpectedRecords.create(coll)

    base.assertStream(stream.filter(new FilterFunction[Int] {
      override def filter(t: Int): Boolean = false
    }), matcher)
    an[AssertionError] shouldBe thrownBy(base.executeTest())
  }



  it should "run a basic test with two sinks" in {
    val base = new DataStreamTestBase
    base.initialize()

    val coll: java.util.Collection[Int] = List(1, 2, 3, 4)
    val stream1: DataStream[Int] = base.createTestStream(coll)
    val stream2: DataStream[Int] = base.createTestStream(coll)
    val matcher = ExpectedRecords.create(coll)

    base.assertStream(stream1, matcher)
    base.assertStream(stream2, matcher)
    base.executeTest()
  }

  it should "fail a basic test with two sinks" in {
    val base = new DataStreamTestBase
    base.initialize()

    val coll: java.util.Collection[Int] = List(1, 2, 3, 4)
    val stream1: DataStream[Int] = base.createTestStream(coll)
    val stream2: DataStream[Int] = base.createTestStream(List(1, 2, 3))
    val matcher = ExpectedRecords.create(coll)

    base.assertStream(stream1, matcher)
    base.assertStream(stream2, matcher)
    an[AssertionError] shouldBe thrownBy(base.executeTest())
  }

}
