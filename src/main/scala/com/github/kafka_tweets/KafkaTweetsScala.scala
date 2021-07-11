package com.github.kafka_tweets

import java.time.Duration
import java.util.{Collections, Properties}

import model.Tweet

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._


object KafkaTweetsScala extends App {

  import Serdes._
//  import org.apache.kafka.streams.scala.serialization.Serdes._

  implicit val tweetSerde: Serde[Tweet] = getJsonSerde()

  val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-tweets-scala-01")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p
  }

  val builder: StreamsBuilder = new StreamsBuilder
  val rawTweets: KStream[String, Tweet] = builder.stream[String, Tweet]("twitter_json_01")
  val noFranz: KStream[String, Tweet] = rawTweets
    .filter((_: String, tweet: Tweet) => !tweet.getText.toLowerCase.contains("franz"))

  val noFranzTopic = "no_franz_kafka_tweets"
  noFranz.to(noFranzTopic)

  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.cleanUp()
  streams.start()

  // print the topology
  System.out.println(streams.toString)

  // shutdown hook to correctly close the streams application
  sys.ShutdownHookThread {
    streams.close(Duration.ofSeconds(10))
  }

  private def getJsonSerde(): Serde[Tweet] = {
    import io.confluent.kafka.serializers.{KafkaJsonDeserializer, KafkaJsonSerializer}
    import org.apache.kafka.common.serialization.{Serializer,Deserializer}
    val serdeProps: java.util.Map[String, Object] = Collections.singletonMap("json.value.type", classOf[Tweet])
    val mySerializer: Serializer[Tweet] = new KafkaJsonSerializer[Tweet]()
    mySerializer.configure(serdeProps, false)
    val myDeserializer: Deserializer[Tweet] = new KafkaJsonDeserializer[Tweet]()
    myDeserializer.configure(serdeProps, false)
    Serdes.fromFn(
      (topic, data) => mySerializer.serialize(topic, data),
      (topic, bytes) => Option(myDeserializer.deserialize(topic, bytes))
    )
  }
}
