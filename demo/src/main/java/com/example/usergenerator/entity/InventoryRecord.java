package com.example.usergenerator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inventory_record")
public class InventoryRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String drugId;

    private String inventoryId;

    private String changeType;

    private Integer changeQuantity;

    private Integer beforeQuantity;

    private Integer afterQuantity;

    private Long operatorId;

    private String reason;

    private LocalDateTime createTime;
}
