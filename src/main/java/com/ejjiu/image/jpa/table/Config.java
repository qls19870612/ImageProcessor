package com.ejjiu.image.jpa.table;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * 创建人  liangsong
 * 创建时间 2020/11/16 14:26
 */
@Getter
@Setter
@Entity
@Table(name = "config")
public class Config {
    @Id
    private String key;

    private String value;
}
