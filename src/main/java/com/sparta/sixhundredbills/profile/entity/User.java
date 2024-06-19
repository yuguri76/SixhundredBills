package com.sparta.sixhundredbills.profile.entity;

import com.sparta.sixhundredbills.TimeStamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "user")
@Entity
@Getter
@NoArgsConstructor
public class User extends TimeStamp {
    // 임시 구현
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
