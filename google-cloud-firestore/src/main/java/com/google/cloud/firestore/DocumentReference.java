/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.firestore;

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptions;
import com.google.cloud.firestore.v1beta1.PagedResponseWrappers.ListCollectionIdsPagedResponse;
import com.google.firestore.v1beta1.ListCollectionIdsRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A DocumentReference refers to a document location in a Firestore database and can be used to
 * write, read, or listen to the location. There may or may not exist a document at the referenced
 * location. A DocumentReference can also be used to create a CollectionReference to a
 * subcollection.
 */
public final class DocumentReference {

  private final ResourcePath path;
  private final FirestoreImpl firestore;

  DocumentReference(FirestoreImpl firestore, ResourcePath path) {
    this.path = path;
    this.firestore = firestore;
  }

  /*
   * Gets the Firestore instance associated with this document reference.
   *
   * @return The Firestore instance.
   */
  @Nonnull
  public Firestore getFirestore() {
    return firestore;
  }

  /**
   * The id of a document refers to the last component of path pointing to a document, e.g.
   * "document-id" in "projects/project-id/databases/database-id/document-id".
   *
   * @return The ID of the document.
   */
  @Nonnull
  public String getId() {
    return path.getId();
  }

  /**
   * A reference to the Collection to which this DocumentReference belongs to.
   *
   * @return The parent Collection.
   */
  @Nonnull
  public CollectionReference getParent() {
    return new CollectionReference(firestore, path.getParent());
  }

  /**
   * A string representing the path of the referenced document (relative to the root of the
   * database).
   *
   * @return The slash-separated path relative to the root of the database.
   */
  @Nonnull
  public String getPath() {
    return path.toString();
  }

  /**
   * Gets a CollectionReference instance that refers to the collection that is a child of this
   * document.
   *
   * @param collectionPath A relative and slash-separated path to a collection.
   * @return The CollectionReference instance.
   */
  @Nonnull
  public CollectionReference collection(@Nonnull String collectionPath) {
    return new CollectionReference(firestore, path.append(collectionPath));
  }

  /**
   * Extracts the first result from an ApiFuture with list of results.
   *
   * @param results A list with results.
   * @return An ApiFuture with the first WriteResult.
   */
  private <T> ApiFuture<T> extractFirst(ApiFuture<List<T>> results) {
    return ApiFutures.transform(
        results,
        new ApiFunction<List<T>, T>() {
          @Override
          public T apply(List<T> results) {
            return results.isEmpty() ? null : results.get(0);
          }
        });
  }

  /**
   * Creates a new Document at the DocumentReference's Location. It fails the write if the document
   * exists.
   *
   * @param fields A map of the fields and values for the document.
   * @return An ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> create(@Nonnull Map<String, Object> fields) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.create(this, fields).commit());
  }

  /**
   * Creates a new Document at the DocumentReference location. It fails the write if the document
   * exists.
   *
   * @param pojo A map of the fields and values for the document.
   * @return An ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> create(@Nonnull Object pojo) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.create(this, pojo).commit());
  }

  /**
   * Overwrites the document referred to by this DocumentReference. If no document exists yet, it
   * will be created. If a document already exists, it will be overwritten.
   *
   * @param fields A map of the fields and values for the document.
   * @return An ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> set(@Nonnull Map<String, Object> fields) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.set(this, fields).commit());
  }

  /**
   * Writes to the document referred to by this DocumentReference. If the document does not yet
   * exist, it will be created. If you pass {@link SetOptions}, the provided data can be merged into
   * an existing document.
   *
   * @param fields A map of the fields and values for the document.
   * @param options An object to configure the set behavior.
   * @return An ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> set(
      @Nonnull Map<String, Object> fields, @Nonnull SetOptions options) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.set(this, fields, options).commit());
  }

  /**
   * Overwrites the document referred to by this DocumentReference. If no document exists yet, it
   * will be created. If a document already exists, it will be overwritten.
   *
   * @param pojo The POJO that will be used to populate the document contents.
   * @return An ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> set(@Nonnull Object pojo) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.set(this, pojo).commit());
  }

  /**
   * Writes to the document referred to by this DocumentReference. If the document does not yet
   * exist, it will be created. If you pass {@link SetOptions}, the provided data can be merged into
   * an existing document.
   *
   * @param pojo The POJO that will be used to populate the document contents.
   * @param options An object to configure the set behavior.
   * @return An ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> set(@Nonnull Object pojo, @Nonnull SetOptions options) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.set(this, pojo, options).commit());
  }

  /**
   * Updates fields in the document referred to by this DocumentReference. If the document doesn't
   * exist yet, the update will fail.
   *
   * @param fields A Map containing the fields and values with which to update the document.
   * @return An ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> update(@Nonnull Map<String, Object> fields) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.update(this, fields).commit());
  }

  /**
   * Updates fields in the document referred to by this DocumentReference. If the document doesn't
   * exist yet, the update will fail.
   *
   * @param fields A map containing the fields and values with which to update the document.
   * @param options Preconditions to enforce on this update.
   * @return An ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> update(@Nonnull Map<String, Object> fields, Precondition options) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.update(this, fields, options).commit());
  }

  /**
   * Updates the fields in the document referred to by this DocumentReference. If the document
   * doesn't exist yet, the update will fail.
   *
   * @param field The first field to set
   * @param value The first value to set
   * @param moreFieldsAndValues String and Object pairs with more fields to be set.
   * @return An ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> update(
      @Nonnull String field, @Nullable Object value, Object... moreFieldsAndValues) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.update(this, field, value, moreFieldsAndValues).commit());
  }

  /**
   * Updates the fields in the document referred to by this DocumentReference. If the document
   * doesn't exist yet, the update will fail.
   *
   * @param fieldPath The first field to set
   * @param value The first value to set
   * @param moreFieldsAndValues String and Object pairs with more fields to be set.
   * @return A ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> update(
      @Nonnull FieldPath fieldPath, @Nullable Object value, Object... moreFieldsAndValues) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.update(this, fieldPath, value, moreFieldsAndValues).commit());
  }

  /**
   * Updates the fields in the document referred to by this DocumentReference. If the document
   * doesn't exist yet, the update will fail.
   *
   * @param options Preconditions to enforce on this update.
   * @param field The first field to set
   * @param value The first value to set
   * @param moreFieldsAndValues String and Object pairs with more fields to be set.
   * @return A ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> update(
      @Nonnull Precondition options,
      @Nonnull String field,
      @Nullable Object value,
      Object... moreFieldsAndValues) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(
        writeBatch.update(this, options, field, value, moreFieldsAndValues).commit());
  }

  /**
   * Updates the fields in the document referred to by this DocumentReference. If the document
   * doesn't exist yet, the update will fail.
   *
   * @param options Preconditions to enforce on this update.
   * @param fieldPath The first field to set
   * @param value The first value to set
   * @param moreFieldsAndValues String and Object pairs with more fields to be set.
   * @return A ApiFuture that will be resolved when the write finishes.
   */
  @Nonnull
  public ApiFuture<WriteResult> update(
      @Nonnull Precondition options,
      @Nonnull FieldPath fieldPath,
      @Nullable Object value,
      Object... moreFieldsAndValues) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(
        writeBatch.update(this, options, fieldPath, value, moreFieldsAndValues).commit());
  }

  /**
   * Deletes the document referred to by this DocumentReference.
   *
   * @param options Preconditions to enforce for this delete.
   * @return An ApiFuture that will be resolved when the delete completes.
   */
  @Nonnull
  public ApiFuture<WriteResult> delete(@Nonnull Precondition options) {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.delete(this, options).commit());
  }

  /**
   * Deletes the document referred to by this DocumentReference.
   *
   * @return An ApiFuture that will be resolved when the delete completes.
   */
  @Nonnull
  public ApiFuture<WriteResult> delete() {
    WriteBatch writeBatch = firestore.batch();
    return extractFirst(writeBatch.delete(this).commit());
  }

  /**
   * Reads the document referenced by this DocumentReference. If the document doesn't exist, the
   * get() will return an an empty DocumentSnapshot.
   *
   * @return An ApiFuture that will be resolved with the contents of the Document at this
   *     DocumentReference, or a failure if the document does not exist.
   */
  @Nonnull
  public ApiFuture<DocumentSnapshot> get() {
    return extractFirst(firestore.getAll(this));
  }

  /**
   * Fetches the subcollections that are direct children of this document.
   *
   * @throws FirestoreException if the Iterable could not be initialized.
   * @return An Iterable that can be used to fetch all subcollections.
   */
  public Iterable<CollectionReference> getCollections() {
    ListCollectionIdsRequest.Builder request = ListCollectionIdsRequest.newBuilder();
    request.setParent(path.toString());
    final ListCollectionIdsPagedResponse response;

    try {
      response =
          ApiExceptions.callAndTranslateApiException(
              firestore.sendRequest(
                  request.build(), firestore.getClient().listCollectionIdsPagedCallable()));
    } catch (ApiException exception) {
      throw FirestoreException.apiException(exception);
    }

    return new Iterable<CollectionReference>() {
      @Override
      @Nonnull
      public Iterator<CollectionReference> iterator() {
        final Iterator<String> iterator = response.iterateAll().iterator();
        return new Iterator<CollectionReference>() {
          @Override
          public boolean hasNext() {
            return iterator.hasNext();
          }

          @Override
          public CollectionReference next() {
            return DocumentReference.this.collection(iterator.next());
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException("remove");
          }
        };
      }
    };
  }

  @Override
  public String toString() {
    return String.format("DocumentReference{path=%s}", path);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocumentReference that = (DocumentReference) o;
    return Objects.equals(path, that.path) && Objects.equals(firestore, that.firestore);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, firestore);
  }
}
