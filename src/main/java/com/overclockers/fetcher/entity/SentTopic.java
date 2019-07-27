package com.overclockers.fetcher.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sent_topic")
public class SentTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "application_user_id", referencedColumnName = "id", nullable = false)
    private ApplicationUser applicationUser;
    @OneToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id", nullable = false)
    private ForumTopic forumTopic;
    @Column(name = "created_datetime", nullable = false)
    private ZonedDateTime createdDatetime;

    /*
    Custom equals and hash code were implemented to indicate ident sent topics only
    by application_user_id and topic_id
     */
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof SentTopic)) return false;
        final SentTopic other = (SentTopic) o;
        return (Objects.equals(this.getApplicationUser().getId(), other.getApplicationUser().getId())
                && Objects.equals(this.getForumTopic().getId(), other.getForumTopic().getId())
        );
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object thisApplicationUserId = this.getApplicationUser().getId();
        result = result * PRIME + (thisApplicationUserId == null ? 43 : thisApplicationUserId.hashCode());
        final Object thisForumTopicId = this.getForumTopic().getId();
        result = result * PRIME + (thisForumTopicId == null ? 43 : thisForumTopicId.hashCode());
        return result;
    }
}
