package lang;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@JsonSerialize
public class RecordResponse implements Serializable {
        String id;
        int accountId;
        String customerId;
        String contactId;
        int dataSourceId;
        int dataSetId;
        String batchId;
        String type;
        List<String> subtypes;
        String collectionName;
        String startTime;
        String endTime;
        String state;
        String processTime;
        String message;
}
