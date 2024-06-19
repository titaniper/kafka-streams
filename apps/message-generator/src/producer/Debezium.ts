import { Kafka } from 'kafkajs';

const brokers = [`localhost:9092`]

const kafka = new Kafka({
  clientId: 'kafkajs-producer',
  brokers: brokers,
  ssl: false,
  // sasl: false,
  // brokers: string[] | BrokersFunction
  // ssl?: tls.ConnectionOptions | boolean
  // sasl?: SASLOptions | Mechanism
  // clientId?: string
  // connectionTimeout?: number
  // authenticationTimeout?: number
  // reauthenticationThreshold?: number
  // requestTimeout?: number
  // enforceRequestTimeout?: boolean
  // retry?: RetryOptions
  // socketFactory?: ISocketFactory
  // logLevel?: logLevel
  // logCreator?: logCreator
})

const producer = kafka.producer(
  // createPartitioner: (config: {
  //   topic: string
  //   partitionMetadata: PartitionMetadata[]
  //   message: Message
  // }) +> void,
  // retry?: RetryOptions
  // metadataMaxAge?: number
  // allowAutoTopicCreation?: boolean
  // idempotent?: boolean
  // transactionalId?: string
  // transactionTimeout?: number
  // maxInFlightRequests?: number
)

const run = async () => {
  // Producing
  await producer.connect()
  await producer.send({
    // topic: string
    // messages: Message[]
    // acks?: number
    // timeout?: number
    // compression?: CompressionTypes
    // test_unsubscribing_topic
    // test-unsubscribing-topic-group2
    // topic: 'test_unsubscribing_topic',
    topic: 'streams-plaintext-input',
    /**
     * 0=서버 응답 기다리지 않음, 전송 보장 없음, 처리량 높아지겠지만 메시지 유실 괜찮은 경우만 사용
     * 1=파티션 리더에 저장되면 응답 받음. 리더 장애시 메시지 유실 가능(팔로워에 복제되지 않은 상태에서 에러날 경우)
     * all or -1= min.insync.replicas 리플리카에 저장되면 응답 받음, 
     */
    // acks: []; 
    messages: [
      {
        headers: {
          name: 'test',
        },
        key: JSON.stringify({
          schema: {
              type: 'struct',
              fields: [
                  {
                      type: 'int64',
                      optional: false,
                      field: 'id',
                  },
              ],
              optional: false,
              name: 'debezium.tover.ddd_event.Key',
          },
          payload: {
              id: 1214961,
          },
        }),
        value: JSON.stringify({
            schema: {
                type: 'struct',
                optional: false,
                name: 'debezium.tover.ddd_event.Envelope',
                version: 1,
            },
            payload: {
                before: null,
                after: {
                    id: 1214961,
                    type: 'NotificationQueuedEvent',
                    occurredAt: 1718785628000,
                    txId: '466daa33-0a60-48ed-8c99-b473bc43f72e',
                    createdAt: 1718785629049095,
                    updatedAt: 1718785629049095,
                    data: '{"id":"1"}',
                    actorId: '5431',
                    // metadata: '{"channel":"kafka","from":"tover","to":"dynamic-partition-forwarder-app-typeA","enabled":true,"partition":1}',
                    metadata: '{"channel":"kafka","from":"tover","to":"dynamic-partition-forwarder-app-typeA","enabled":true}',
                },
                source: {
                    version: '2.1.3.Final',
                    connector: 'mysql',
                    name: 'debezium',
                    ts_ms: 1718785629000,
                    snapshot: 'false',
                    db: 'tover',
                    sequence: null,
                    table: 'ddd_event',
                    server_id: 39870371,
                    gtid: null,
                    file: 'mysql-bin-changelog.000992',
                    pos: 14959707,
                    row: 0,
                    thread: 70780,
                    query: null,
                },
            },
        }), 
        // headers: {}, 
        // partition: 0, 
        // timestamp: 0,
    }],
    // compression: CompressionTypes.ZSTD,
  })
}

run().catch(console.error)