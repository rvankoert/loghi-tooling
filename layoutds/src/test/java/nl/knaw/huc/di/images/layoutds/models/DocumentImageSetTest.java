package nl.knaw.huc.di.images.layoutds.models;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DocumentImageSetTest {
    @Test
    public void addSubSetAllowsImageSetWithoutSubSet() {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        final DocumentImageSet subSet = new DocumentImageSet();

        addSubSet(documentImageSet, subSet);

        assertThat(documentImageSet.getSubSets(), hasItem(hasProperty("uuid", equalTo(subSet.getUuid()))));
    }

    @Test
    public void addSubSetAllowsImageSetWithSubSet() {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        final DocumentImageSet subSet = new DocumentImageSet();
        addSubSet(subSet, new DocumentImageSet());

        addSubSet(documentImageSet, subSet);

        assertThat(documentImageSet.getSubSets(), hasItem(hasProperty("uuid", equalTo(subSet.getUuid()))));
    }

    @Test
    public void addSubSetAllowsImageSetWhenTheImageSetToAddToAlreadyGotSubsetsWithSubSets() {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        final DocumentImageSet subSet = new DocumentImageSet();
        final DocumentImageSet subSubSet = new DocumentImageSet();
        addSubSet(subSet, subSubSet);
        addSubSet(documentImageSet, subSet);
        final DocumentImageSet subSetToAdd = new DocumentImageSet();

        addSubSet(documentImageSet, subSetToAdd);

        assertThat(documentImageSet.getSubSets(), hasItem(hasProperty("uuid", equalTo(subSetToAdd.getUuid()))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubSetIsNotAllowedForSubsetsWithMoreThan3LevelsOfSubSets() {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        final DocumentImageSet subSet = new DocumentImageSet();
        final DocumentImageSet subSubSet = new DocumentImageSet();
        addSubSet(subSubSet, new DocumentImageSet());
        addSubSet(subSet, subSubSet);

        addSubSet(documentImageSet, subSet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubSetIsNotAllowedWhenSubSetIsOnAThirdLevelInATree() {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        final DocumentImageSet subSet = new DocumentImageSet();
        final DocumentImageSet subSubSet = new DocumentImageSet();
        addSubSet(subSet, subSubSet);
        addSubSet(documentImageSet, subSet);

        addSubSet(subSubSet, new DocumentImageSet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubSetIsNotAllowedWhenTreeWillBeBiggerThan3Levels() {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        final DocumentImageSet subSet = new DocumentImageSet();
        final DocumentImageSet subSetToAdd = new DocumentImageSet();
        addSubSet(subSetToAdd, new DocumentImageSet());

        addSubSet(documentImageSet, subSet);

        addSubSet(subSet, subSetToAdd);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubSetIsNotAllowedWhenImageSetIsASuperSet() {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        final DocumentImageSet subSet = new DocumentImageSet();
        addSubSet(documentImageSet, subSet);

        addSubSet(subSet, documentImageSet);

    }

    // Simulate what happens when DocumentImageSet is persisted
    private void addSubSet(DocumentImageSet documentImageSet, DocumentImageSet subSet) {
        documentImageSet.addSubSet(subSet);
        subSet.addSuperSet(documentImageSet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubSetIsNotAllowedWhenImageSetIsAlreadyPartOfABranch() {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        final DocumentImageSet subSet = new DocumentImageSet();
        final DocumentImageSet subSubSet = new DocumentImageSet();
        addSubSet(subSet, subSubSet);
        addSubSet(documentImageSet, subSet);

        addSubSet(documentImageSet, subSubSet);

    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubSetIsNotAllowedWhenImageSetIsAlreadyPartOfAnotherBranchOfASuperSet() {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        final DocumentImageSet subSet = new DocumentImageSet();
        final DocumentImageSet subSubSet = new DocumentImageSet();
        addSubSet(documentImageSet, subSubSet);
        addSubSet(documentImageSet, subSet);

        addSubSet(subSet, subSubSet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSubsetDoesNotAllowASubsetToBeAddedToOneOfItsSubsets() {
        final DocumentImageSet documentImageSet = new DocumentImageSet();
        final DocumentImageSet subSet = new DocumentImageSet();
        addSubSet(subSet, documentImageSet);

        addSubSet(documentImageSet, subSet);
    }
}
