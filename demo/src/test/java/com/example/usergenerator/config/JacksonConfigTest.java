package com.example.usergenerator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("JacksonConfig 单元测试")
class JacksonConfigTest {

    private static final String EXPECTED_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Test
    @DisplayName("测试 jacksonCustomizer 方法返回非空对象")
    void testJackonCustomizerReturnsNotNull() {
        JacksonConfig config = new JacksonConfig();
        Jackson2ObjectMapperBuilderCustomizer customizer = config.jacksonCustomizer();
        assertNotNull(customizer, "jacksonCustomizer should not return null");
    }

    @Test
    @DisplayName("测试 jacksonCustomizer 返回的 Customizer 可以正确配置 ObjectMapper")
    void testJackonCustomizerConfiguresObjectMapperCorrectly() {
        JacksonConfig config = new JacksonConfig();
        Jackson2ObjectMapperBuilderCustomizer customizer = config.jacksonCustomizer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        customizer.customize(objectMapper);

        assertNotNull(objectMapper);
    }

    @Test
    @DisplayName("测试 LocalDateTime 序列化功能")
    void testLocalDateTimeSerialization() throws Exception {
        JacksonConfig config = new JacksonConfig();
        Jackson2ObjectMapperBuilderCustomizer customizer = config.jacksonCustomizer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        customizer.customize(objectMapper);

        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        String json = objectMapper.writeValueAsString(dateTime);

        assertEquals("\"2024-01-15 10:30:45\"", json, "LocalDateTime should be serialized with correct format");
    }

    @Test
    @DisplayName("测试 LocalDateTime 反序列化功能")
    void testLocalDateTimeDeserialization() throws Exception {
        JacksonConfig config = new JacksonConfig();
        Jackson2ObjectMapperBuilderCustomizer customizer = config.jacksonCustomizer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        customizer.customize(objectMapper);

        String json = "\"2024-01-15 10:30:45\"";
        LocalDateTime dateTime = objectMapper.readValue(json, LocalDateTime.class);

        LocalDateTime expected = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        assertEquals(expected, dateTime, "LocalDateTime should be deserialized with correct format");
    }

    @Test
    @DisplayName("测试时间边界值 - 最小时间 00:00:00")
    void testLocalDateTimeBoundaryMinTime() throws Exception {
        JacksonConfig config = new JacksonConfig();
        Jackson2ObjectMapperBuilderCustomizer customizer = config.jacksonCustomizer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        customizer.customize(objectMapper);

        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        String json = objectMapper.writeValueAsString(dateTime);

        assertEquals("\"2024-01-01 00:00:00\"", json);
    }

    @Test
    @DisplayName("测试时间边界值 - 最大时间 23:59:59")
    void testLocalDateTimeBoundaryMaxTime() throws Exception {
        JacksonConfig config = new JacksonConfig();
        Jackson2ObjectMapperBuilderCustomizer customizer = config.jacksonCustomizer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        customizer.customize(objectMapper);

        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
        String json = objectMapper.writeValueAsString(dateTime);

        assertEquals("\"2024-12-31 23:59:59\"", json);
    }

    @Test
    @DisplayName("测试年份边界值 - 最小年份 0001")
    void testLocalDateTimeBoundaryMinYear() throws Exception {
        JacksonConfig config = new JacksonConfig();
        Jackson2ObjectMapperBuilderCustomizer customizer = config.jacksonCustomizer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        customizer.customize(objectMapper);

        LocalDateTime dateTime = LocalDateTime.of(1, 1, 1, 0, 0, 0);
        String json = objectMapper.writeValueAsString(dateTime);

        assertEquals("\"0001-01-01 00:00:00\"", json);
    }

    @Test
    @DisplayName("测试年份边界值 - 最大年份 9999")
    void testLocalDateTimeBoundaryMaxYear() throws Exception {
        JacksonConfig config = new JacksonConfig();
        Jackson2ObjectMapperBuilderCustomizer customizer = config.jacksonCustomizer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        customizer.customize(objectMapper);

        LocalDateTime dateTime = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        String json = objectMapper.writeValueAsString(dateTime);

        assertEquals("\"9999-12-31 23:59:59\"", json);
    }

    @Test
    @DisplayName("测试序列化后再反序列化保持一致")
    void testSerializeThenDeserializeConsistency() throws Exception {
        JacksonConfig config = new JacksonConfig();
        Jackson2ObjectMapperBuilderCustomizer customizer = config.jacksonCustomizer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        customizer.customize(objectMapper);

        LocalDateTime original = LocalDateTime.of(2024, 6, 15, 14, 30, 0);
        String json = objectMapper.writeValueAsString(original);
        LocalDateTime deserialized = objectMapper.readValue(json, LocalDateTime.class);

        assertEquals(original, deserialized, "Serialized and deserialized LocalDateTime should be equal");
    }

    @Test
    @DisplayName("测试毫秒精度处理（秒精度，无毫秒）")
    void testMillisecondPrecision() throws Exception {
        JacksonConfig config = new JacksonConfig();
        Jackson2ObjectMapperBuilderCustomizer customizer = config.jacksonCustomizer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        customizer.customize(objectMapper);

        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 15, 14, 30, 0);
        String json = objectMapper.writeValueAsString(dateTime);

        assertEquals("\"2024-06-15 14:30:00\"", json);
    }

    @Test
    @DisplayName("测试格式字符串常量值正确")
    void testDateTimePatternConstant() {
        JacksonConfig config = new JacksonConfig();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(EXPECTED_DATE_TIME_PATTERN);

        assertNotNull(formatter);
        assertEquals(EXPECTED_DATE_TIME_PATTERN, formatter.toString());
    }

    @Test
    @DisplayName("测试 DateTimeFormatter 可以正确解析符合格式的字符串")
    void testDateTimeFormatterParsing() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(EXPECTED_DATE_TIME_PATTERN);

        LocalDateTime dateTime = LocalDateTime.parse("2024-03-20 08:15:30", formatter);
        LocalDateTime expected = LocalDateTime.of(2024, 3, 20, 8, 15, 30);

        assertEquals(expected, dateTime);
    }

    @Test
    @DisplayName("测试 DateTimeFormatter 可以正确格式化 LocalDateTime")
    void testDateTimeFormatterFormatting() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(EXPECTED_DATE_TIME_PATTERN);

        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 20, 8, 15, 30);
        String formatted = dateTime.format(formatter);

        assertEquals("2024-03-20 08:15:30", formatted);
    }

    @Test
    @DisplayName("测试格式器生成的序列化器类型正确")
    void testSerializerType() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(EXPECTED_DATE_TIME_PATTERN);
        LocalDateTimeSerializer serializer = new LocalDateTimeSerializer(formatter);

        assertInstanceOf(LocalDateTimeSerializer.class, serializer);
    }

    @Test
    @DisplayName("测试格式器生成的反序列化器类型正确")
    void testDeserializerType() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(EXPECTED_DATE_TIME_PATTERN);
        LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer(formatter);

        assertInstanceOf(LocalDateTimeDeserializer.class, deserializer);
    }

    @Test
    @DisplayName("测试 multiple 多次调用 jacksonCustomizer 返回相同结果")
    void testMultipleCallsReturnConsistentResult() {
        JacksonConfig config = new JacksonConfig();

        Jackson2ObjectMapperBuilderCustomizer customizer1 = config.jacksonCustomizer();
        Jackson2ObjectMapperBuilderCustomizer customizer2 = config.jacksonCustomizer();

        assertNotNull(customizer1);
        assertNotNull(customizer2);
    }

    @Test
    @DisplayName("测试同一个 ObjectMapper 可以被多次自定义")
    void testSameObjectMapperCanBeCustomizedMultipleTimes() {
        JacksonConfig config = new JacksonConfig();
        Jackson2ObjectMapperBuilderCustomizer customizer = config.jacksonCustomizer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        customizer.customize(objectMapper);
        customizer.customize(objectMapper);

        assertNotNull(objectMapper);
    }
}
