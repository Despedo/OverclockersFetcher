package com.overclockers.fetcher.entity;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "topic")
public class ForumTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    @JoinColumn(name = "user_forum_id")
    @ManyToOne(targetEntity = ForumUser.class)
    private ForumUser user;
    @Column(name = "location")
    private String location;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "forum_id", nullable = false)
    private Long topicForumId;
    @Column(name = "topic_created_datetime", nullable = false)
    private ZonedDateTime topicCreatedDateTime;
    @Column(name = "topic_updated_datetime")
    private ZonedDateTime topicUpdatedDateTime;
    @Column(name = "created_datetime", nullable = false)
    private ZonedDateTime createdDateTime;
    @OneToOne(mappedBy = "forumTopic")
    private SentTopic sentTopic;

    /*
    Custom equals and hash code were implemented for current work with collections
    to indicate updated topics. @EqualsAndHashCode from lombock doesnt properly work
    with ZonedDateTime.
     */
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ForumTopic)) return false;
        final ForumTopic other = (ForumTopic) o;
        return (Objects.equals(this.getLocation(), other.getLocation())
                && Objects.equals(this.getTitle(), other.getTitle())
                && Objects.equals(this.getTopicForumId(), other.getTopicForumId())
                && compareDateTime(this.getTopicCreatedDateTime(), other.getTopicCreatedDateTime())
                && compareDateTime(this.getTopicUpdatedDateTime(), other.getTopicUpdatedDateTime())
        );
    }

    private boolean compareDateTime(ZonedDateTime thisTopicCreatedDateTime, ZonedDateTime otherTopicCreatedDateTime) {
        return thisTopicCreatedDateTime == null ? otherTopicCreatedDateTime == null :
                (otherTopicCreatedDateTime != null && thisTopicCreatedDateTime.toInstant().compareTo(otherTopicCreatedDateTime.toInstant()) == 0);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object thisLocation = this.getLocation();
        result = result * PRIME + (thisLocation == null ? 43 : thisLocation.hashCode());
        final Object thisTitle = this.getTitle();
        result = result * PRIME + (thisTitle == null ? 43 : thisTitle.hashCode());
        final Object thisTopicForumId = this.getTopicForumId();
        result = result * PRIME + (thisTopicForumId == null ? 43 : thisTopicForumId.hashCode());
        final Object thisTopicCreatedDateTime = this.getTopicCreatedDateTime();
        result = result * PRIME + (thisTopicCreatedDateTime == null ? 43 : thisTopicCreatedDateTime.hashCode());
        final Object thisTopicUpdatedDateTime = this.getTopicUpdatedDateTime();
        result = result * PRIME + (thisTopicUpdatedDateTime == null ? 43 : thisTopicUpdatedDateTime.hashCode());
        return result;
    }
}
