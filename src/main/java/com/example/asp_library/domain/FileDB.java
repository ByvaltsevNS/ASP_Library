package com.example.asp_library.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="fileDB")
@NoArgsConstructor
@Getter @Setter
public class FileDB {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    private String type;
    private String parent;
    private boolean isTask;
    @Lob
    private byte[] data;

    public FileDB(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;

        this.parent = "";
        this.isTask = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDB fileDB = (FileDB) o;
        return Arrays.equals(data, fileDB.data);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(data);
        return result;
    }
}
