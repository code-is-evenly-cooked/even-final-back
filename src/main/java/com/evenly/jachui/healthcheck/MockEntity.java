package com.evenly.jachui.healthcheck;

import jakarta.persistence.*;

@Entity
@Table(name = "health_check")
public class MockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
