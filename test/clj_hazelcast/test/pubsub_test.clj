(ns clj-hazelcast.test.pubsub-test
  (:import java.util.UUID)
  (:use clojure.test)
  (:require [clj-hazelcast.core :as hz]
            [clj-hazelcast.pubsub :as pubsub]))

(defn fixture [f]
  (hz/init)
  (f)
  (hz/shutdown))

(use-fixtures :once fixture)

(deftest subscribe-publish-unsubscribe
  (let [topic-name (str "test-topic-" (UUID/randomUUID))
        message-captor (promise)
        subscription-id (pubsub/subscribe
                          topic-name
                          @hz/hazelcast
                          (fn -msg-sub-fn [message]
                            (deliver message-captor message)))
        test-object {:some "message"}]

    (pubsub/publish
      topic-name
      @hz/hazelcast
      test-object)

    (let [captured-message (deref
                             message-captor
                             10000
                             :timeout)]
      (is (= (:object captured-message)
            test-object))
      (is (> (:publish-time captured-message)
            0))
      (is (:publishing-member captured-message)))

    (is
      (pubsub/unsubscribe
        topic-name
        @hz/hazelcast
        subscription-id))))
