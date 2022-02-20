package cn.cjx913.httpdiffy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Table
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Id
    private Long id;
    private String username;
}
