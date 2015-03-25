(ns clj-hazelcast.pubsub
  (:import [com.hazelcast.core HazelcastInstance
                               MessageListener
                               ITopic]))

(defn get-topic
  [^String topic-name ^HazelcastInstance hz-instance]
  (.getTopic
    hz-instance
    topic-name))

(defn- fn->message-listener
  [listener-fn]
  (reify MessageListener
    (onMessage [this message]
      (listener-fn
        {:object (.getMessageObject message)
         :publish-time (.getPublishTime message)
         :publishing-member (.getPublishingMember message)}))))

(defn subscribe
  ([^ITopic topic listener-fn]
    (.addMessageListener
      topic
      (fn->message-listener listener-fn)))
  ([topic-name ^HazelcastInstance hz-instance listener-fn]
    (let [topic (get-topic topic-name hz-instance)]
      (subscribe
        topic
        listener-fn))))

(defn unsubscribe
  ([^ITopic topic subscription-id]
    (.removeMessageListener
      topic
      subscription-id))
  ([topic-name ^HazelcastInstance hz-instance subscription-id]
    (let [topic (get-topic topic-name hz-instance)]
      (unsubscribe
        topic
        subscription-id))))

(defn publish
  ([^ITopic topic message-object]
    (.publish
      topic
      message-object))
  ([topic-name ^HazelcastInstance hz-instance message-object]
    (let [topic (get-topic topic-name hz-instance)]
      (publish
        topic
        message-object))))
