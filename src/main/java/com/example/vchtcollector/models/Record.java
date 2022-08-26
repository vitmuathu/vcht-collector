package com.example.vchtcollector.models;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "records")
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    private String session;

    @NotNull
    @Column(columnDefinition="TEXT")
    private String message;


    @NotNull
    private Long id_next;
    public Record(String session, String message, Long id_next) {
        this.session = session;
        this.message = message;
        this.id_next = id_next;
    }
}
