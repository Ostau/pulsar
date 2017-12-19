#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

CLASSPATH=`pwd`/api-examples/target/pulsar-functions-api-examples.jar bin/pulsar-functions functions run --function-config conf/example.yml --sink-topic persistent://sample/standalone/ns1/test_result --source-topic persistent://sample/standalone/ns1/test_src --serde-classname org.apache.pulsar.functions.runtime.container.SimpleStringSerDe --function-classpath `pwd`/api-examples/target/pulsar-functions-api-examples.jar