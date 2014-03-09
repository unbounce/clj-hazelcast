# Clj-hazelcast

Clojure library for Hazelcast, an open source clustering and highly scalable data distribution
platform for Java.

# TravisCI Status

[![Build Status](https://travis-ci.org/runa-labs/clj-hazelcast.png)](https://travis-ci.org/runa-labs/clj-hazelcast)

# Usage

Clj hazelcast comes preconfigured out of the box and allows adding members as an option.
For example, The init function takes a map, {:peers [hostname1 hostname2 ...]}, 
and starts a peer node on them as the member of the cluster.

This library supports synchronization of clj-kryo (another open source library 
from Runa labs) serializable data structures across network. It also supports 
event driven communication among them. A typical use case would to sync key/value
pair among the peers. For example:

Suppose you have following data in all nodes:   (def test-map (atom nil))
You can put new values to this map on host 1:   (hazelcast/put! @test-map :baz "foobar")

Then you can retrieve "foobar" from host 2:    (:baz (hazelcast/get-map "your.name.space.test-map"))

Furthermore, you can add listener to that data to perform all sorts of callbacks. 
For example:

```clj
(let [events (atom [])
      listener-fn (fn [& event]
                    (swap! events conj event))
      listener (hazelcast/add-entry-listener! @test-map listener-fn)
      result (do
               (hazelcast/put! @test-map :baz "foobar")
               (hazelcast/put! @test-map :foo "bizbang")
               (Thread/sleep 5)
               (count @events))]
  (hazelcast/remove-entry-listener! @test-map listener)
  result)
```

Please refer to the inlcuded test for more detail.

## License

 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 *   the terms of this license.
 *   You must not remove this notice, or any other, from this software.
