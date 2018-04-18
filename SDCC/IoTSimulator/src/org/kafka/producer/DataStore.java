/**
 * 
 */
package org.kafka.producer;

/**
 * @author amitesh
 *
 */
public interface DataStore<T> {

	public boolean storeRawEvent(String jsonData);

	public T getAll();

}
