(ns clj-hazelcast.test.core-with-instance
  (:use [clojure.test])
  (:require
   [clj-hazelcast.core :as hazelcast])
  (:import
   com.hazelcast.core.Hazelcast
   com.hazelcast.core.HazelcastInstance))

(def hazelcast-instance (atom nil))

(defn fixture [f]
  (reset! hazelcast-instance (Hazelcast/newHazelcastInstance))
  (hazelcast/with-instance @hazelcast-instance)
  (f)
  (hazelcast/shutdown))

(use-fixtures :once fixture)

(deftest ensure-provided-instance-used
  "Putting via clj-hazelcast should put in the provided Hazelcast instance"
  (let [map-name "clj-hazelcast.cluster-tests-with-instance.test-map"
        instance-map (.getMap @hazelcast-instance map-name)
        clj-hazelcast-map (hazelcast/get-map map-name)]
    (is (= 456
           (do
             (hazelcast/put! clj-hazelcast-map :bar 456)
             (get instance-map :bar))))))

(deftest get-primitives-with-instance
  (let [map-name "clj-hazelcast.get-primitives-with-instance.test-map"
        list-name "clj-hazelcast.get-primitives-with-instance.test-list"
        set-name "clj-hazelcast.get-primitives-with-instance.test-set"
        queue-name "clj-hazelcast.get-primitives-with-instance.test-queue"]
    (is
      (identical?
        (hazelcast/get-map @hazelcast-instance map-name)
        (.getMap @hazelcast-instance map-name)))
    (is
      (identical?
        (hazelcast/get-list @hazelcast-instance list-name)
        (.getList @hazelcast-instance list-name)))
    (is
      (identical?
        (hazelcast/get-set @hazelcast-instance set-name)
        (.getSet @hazelcast-instance set-name)))
    (is
      (identical?
        (hazelcast/get-queue @hazelcast-instance queue-name)
        (.getQueue @hazelcast-instance queue-name)))))
