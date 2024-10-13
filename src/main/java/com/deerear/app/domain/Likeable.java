package com.deerear.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class Likeable extends ModifiableEntity {

    @Column(name = "like_count", nullable = false)
    private long likeCount = 0;


    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public enum TargetType {
        POST,
        COMMENT
    }

    public abstract TargetType getTargetType();
}
