//package lang;
//
//import com.attensity.sidekick.pipeline.util.SidekickPipelineUtil;
//import com.attensity.sidekick.shared.services.ProcessRequest;
//import com.attensity.util.CalendarUtil;
//import com.incontact.connectors.mongo.MongoConnector;
//import com.incontact.connectors.mongo.exceptions.UnknownDatabaseException;
//import com.mongodb.MongoException;
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoCollection;
//import org.apache.log4j.Logger;
//import org.bson.Document;
//import org.bson.conversions.Bson;
//
//import java.text.SimpleDateFormat;
//
//import static com.mongodb.client.model.Filters.*;
//import static com.mongodb.client.model.Sorts.ascending;
//import static com.mongodb.client.model.Sorts.descending;
//
//
//public class MongoReader
//{
//    public static final String PUBLISHED_AT = "published_at";
//    public static final String DATA_SET_ID = "data_set_id";
//    public static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
//    public static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
//    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//
//    static
//    {
//        MONTH_FORMAT.setTimeZone(CalendarUtil.TIMEZONE);
//        DAY_FORMAT.setTimeZone(CalendarUtil.TIMEZONE);
//        DATE_FORMAT.setTimeZone(CalendarUtil.TIMEZONE);
//    }
//
//    public static final String  PROCESS_REQUEST_FIELD = "processRequest";
//    public static final String  ACCOUNT_ID_FIELD = PROCESS_REQUEST_FIELD + ".accountId";
//    public static final String  DATASET_ID_FIELD = PROCESS_REQUEST_FIELD + ".dataSetId";
//    public static final String  TOPIC_ID_FIELD = PROCESS_REQUEST_FIELD + ".topicId";
//    public static final String  EVENT_TYPE_FIELD = PROCESS_REQUEST_FIELD + ".eventType";
//    public static final String  TIMESTAMP_FIELD = "timestamp";
//    public static final String  STATE_FIELD = "state";
//
//    private Logger logger = Logger.getLogger(MongoReader.class);
//
//    private SidekickPipelineUtil    sidekickPipelineUtil;
//
//    public MongoReader(final SidekickPipelineUtil sidekickPipelineUtil) throws IllegalStateException, MongoException
//    {
//        this.sidekickPipelineUtil = sidekickPipelineUtil;
//    }
//
//    /**
//     * Read from a collection, with the specified limit. Ignore limit if it is -1.
//     *
//     * @param collectionName
//     * @param limit
//     * @return
//     * @throws UnknownDatabaseException
//     */
//    public FindIterable<Document> readCollection(final String collectionName, final Integer limit) throws UnknownDatabaseException
//    {
//        MongoCollection<Document> collection = sidekickPipelineUtil.getMongoConnector().getCollection(collectionName);
//
//        if (limit > -1)
//        {
//            return collection.find().limit(limit);
//        }
//
//        return collection.find();
//    }
//
//    /**
//     * Read from a collection, with the specified limit. Ignore limit if it is -1.
//     *
//     * @param collectionName
//     * @param dataSetId
//     * @param limit
//     * @return
//     * @throws UnknownDatabaseException
//     */
//    public FindIterable<Document> readDataSetFromCollection(final String collectionName, final Long dataSetId, final Integer limit) throws UnknownDatabaseException
//    {
//        if (sidekickPipelineUtil.getMongoConnector().collectionExists(collectionName))
//        {
//            MongoCollection<Document>   collection = sidekickPipelineUtil.getMongoConnector().getCollection(collectionName);
//            Bson                        filter = eq(DATA_SET_ID, dataSetId.toString());
//
//            logger.info(String.format("data set is read from collection '%s'", collectionName));
//
//            if (limit > -1)
//            {
//                return collection.find(filter).limit(limit);
//            }
//
//            return collection.find(filter);
//        }
//        else
//        {
//            logger.warn(String.format("collection '%s' does not exist.", collectionName));
//        }
//
//        return null;
//    }
//
//    public String getMinOrMaxPublishedAt(final Long dataSetId, final String collectionName, final Boolean sortAscending)
//    {
//        Bson sort = descending(PUBLISHED_AT);
//
//        if (sortAscending)
//        {
//            sort = ascending(PUBLISHED_AT);
//        }
//
//        Document record;
//
//        if (dataSetId != -1)
//        {
//            record = sidekickPipelineUtil.getMongoConnector().getCollection(collectionName).find(eq(DATA_SET_ID, dataSetId.toString())).sort(sort).first();
//        }
//        else
//        {
//            record = sidekickPipelineUtil.getMongoConnector().getCollection(collectionName).find().sort(sort).first();
//        }
//
//        if (record != null)
//        {
//            return record.getString(PUBLISHED_AT);
//        }
//
//        return null;
//    }
//
//    public String getMinOrMaxAcquiredAt(final Long dataSetId, final String collectionName, final Boolean sortAscending)
//    {
//        return getMinOrMaxPublishedAt(dataSetId, collectionName, sortAscending);
//    }
//
//    public Integer getMinOrMaxDataSetId(final String collectionName, final Boolean sortAscending)
//    {
//        if (sidekickPipelineUtil.getMongoConnector().collectionExists(collectionName))
//        {
//            Bson sort = descending(DATA_SET_ID);
//
//            if (sortAscending)
//            {
//                sort = ascending(DATA_SET_ID);
//            }
//
//            Document record = sidekickPipelineUtil.getMongoConnector().getCollection(collectionName).find().sort(sort).limit(1).first();
//
//            if (record != null)
//            {
//                return record.getInteger(DATA_SET_ID);
//            }
//        }
//
//        return 0;
//    }
//
//    public MongoCollection getManagerQueue() {
//        return sidekickPipelineUtil.getMongoConnector(sidekickPipelineUtil.getManagerQueueDatabaseName()).getCollection(sidekickPipelineUtil.getManagerQueueCollectionName());
//    }
//
//    public long getNumberOfDuplicateIngestProcessRequests(long dataSetId)
//    {
//        return sidekickPipelineUtil.getMongoReader().getManagerQueue().count(and(eq(DATASET_ID_FIELD, dataSetId),
//                eq(STATE_FIELD, ProcessRequestState.UNPROCESSED.name()),
//                eq(EVENT_TYPE_FIELD, ProcessRequest.EventType.INGEST.name())));
//    }
//
//    public long getNumberOfDuplicateProcessRequests(final Integer accountId,
//                                                    final Integer topicId,
//                                                    final String eventType)
//    {
//        return sidekickPipelineUtil.getMongoReader().getManagerQueue().count(and(eq(ACCOUNT_ID_FIELD, accountId),
//                eq(STATE_FIELD, ProcessRequestState.UNPROCESSED.name()),
//                eq(EVENT_TYPE_FIELD, eventType),
//                topicId != null ? eq(TOPIC_ID_FIELD, topicId) : ne(TOPIC_ID_FIELD, -1)));
//    }
//
//    // States in manager queue documents
//    public enum ProcessRequestState
//    {
//        UNPROCESSED,
//        PROCESSING,
//        RETRIED,
//        WAITING,
//        PROCESSED,
//        FAILED,
//        CANCELLED
//    }
//}
