package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.Publisher;
import edu.teikav.robot.parser.domain.PublisherSpecification;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface PublisherSpecificationRegistry {

    /**
     * Register publisher specification from a document containing a single publisher spec
     * @param inputStream Input file containing specification for a single publisher
     */
    void registerPublisherSpecification(InputStream inputStream);

    /**
     * Register publisher specifications from a document containing more than one publisher specs
     * @param inputStream Input file containing specifications for many publishers
     */
    void registerPublisherSpecifications(InputStream inputStream);

    /**
     * Find a specific publisher specification
     * @param specHashCode Code potentially identifying a specific publisher specification. Code is determined by the
     *                     format of the various term parts that may exist in the vocabulary structure
     * @return Optionally a publisher specification if found
     */
    Optional<PublisherSpecification> findSpecByHashCode(int specHashCode);

    /**
     * Find a specific publisher specification
     * @param publisher Publisher object owning the specification
     * @return Optionally a publisher specification if found
     */
    Optional<PublisherSpecification> findSpecByPublisher(Publisher publisher);

    /**
     * Set the active publisher spec. At any time, only one spec can be active
     * @param spec The active spec
     */
    void setActiveSpec(PublisherSpecification spec);

    /**
     * Get the active publisher spec
     * @return Optionally the active publisher specification if found
     */
    Optional<PublisherSpecification> getActiveSpec();

    /**
     * Get size of Publisher Specifications Registry
     * @return Number of all registered publisher specifications
     */
    int registrySize();

    /**
     * Get all registered publisher specifications
     * @return List of all registered publisher specifications
     */
    List<PublisherSpecification> getAllSpecs();

    /**
     * Remove all registered publisher specifications from Registry
     */
    void removeAllPublisherSpecs();
}
