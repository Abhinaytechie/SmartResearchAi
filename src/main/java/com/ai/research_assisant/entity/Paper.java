package com.ai.research_assisant.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("Papers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Paper {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String title;
    private List<String> authors;
    private String abstractText;
    private String content;
    private List<String> citiations;
    private String fileUrl;
    private String uploadDate;

}
