package com.jiubredeemer.magic.dto.spellbook;

import com.jiubredeemer.magic.entity.ChargesRefillEnum;
import lombok.Data;

@Data
public class RefillRestRequest {
    private ChargesRefillEnum restType;
}
