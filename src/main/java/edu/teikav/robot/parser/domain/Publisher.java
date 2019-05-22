package edu.teikav.robot.parser.domain;

import org.springframework.util.StringUtils;

import java.util.Objects;

public class Publisher {

    private String name;
    private String description;
    private String publicationYear;

    public Publisher() {}

    public Publisher(Publisher publisher) {
        this(publisher.getName(), publisher.getDescription(), publisher.getPublicationYear());
    }

    public Publisher(String name, String description, String publicationYear) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Publisher name cannot be empty");
        }
        this.name = name;
        this.description = description;
        this.publicationYear = publicationYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Publisher publisher = (Publisher) o;
        return Objects.equals(name, publisher.name) &&
                Objects.equals(description, publisher.description) &&
                Objects.equals(publicationYear, publisher.publicationYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, publicationYear);
    }
}
