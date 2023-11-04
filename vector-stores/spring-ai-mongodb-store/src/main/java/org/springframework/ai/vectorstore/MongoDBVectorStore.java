/*
 * Copyright 2023-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.vectorstore;

import com.mongodb.BasicDBObject;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Chris Smith
 */
public class MongoDBVectorStore implements VectorStore {

    private MongoTemplate mongoTemplate;

    private EmbeddingClient embeddingClient;

    private static final String VECTOR_COLLECTION_NAME = "vector_store";

    public MongoDBVectorStore(MongoTemplate mongoTemplate, EmbeddingClient embeddingClient) {
        this.mongoTemplate = mongoTemplate;
        this.embeddingClient = embeddingClient;
        if (!mongoTemplate.collectionExists(VECTOR_COLLECTION_NAME)) {
            mongoTemplate.createCollection(VECTOR_COLLECTION_NAME);
        }
    }

    /**
     * Maps a basicDBObject to a Spring AI Document
     *
     * @param basicDBObject
     * @return
     */
    private Document mapBasicDbObject(BasicDBObject basicDBObject) {
        String id = basicDBObject.getString("_id");
        String content = basicDBObject.getString("content");
        Map<String, Object> metadata = (Map<String, Object>) basicDBObject.get("metadata");
        List<Double> embedding = (List<Double>) basicDBObject.get("embedding");

        Document document = new Document(id, content, metadata);
        document.setEmbedding(embedding);

        return document;
    }

    @Override
    public void add(List<Document> documents) {
        for (Document document : documents) {
            List<Double> embedding = this.embeddingClient.embed(document);
            document.setEmbedding(embedding);
            this.mongoTemplate.save(document, VECTOR_COLLECTION_NAME);
        }
    }

    @Override
    public Optional<Boolean> delete(List<String> idList) {
        Query query = new Query(where("_id").in(idList));

        var deleteRes = this.mongoTemplate.remove(query, VECTOR_COLLECTION_NAME);
        long deleteCount = deleteRes.getDeletedCount();

        return Optional.of(deleteCount == idList.size());
    }

    @Override
    public List<Document> similaritySearch(String query) {
        return this.similaritySearch(query, 4);
    }

    @Override
    public List<Document> similaritySearch(String query, int k) {
        return similaritySearch(query, k, 0.0);
    }

    @Override
    public List<Document> similaritySearch(String query, int k, double threshold) {
        List<Double> queryEmbedding = this.embeddingClient.embed(query);


        Aggregation aggregation = Aggregation.newAggregation(
                new VectorSearchAggregation(queryEmbedding, "embedding", 10, "spring_ai_vector_search", k));
        return this.mongoTemplate.aggregate(aggregation, VECTOR_COLLECTION_NAME, BasicDBObject.class)
                .getMappedResults().stream()
                .map(this::mapBasicDbObject)
                .toList();
    }


}