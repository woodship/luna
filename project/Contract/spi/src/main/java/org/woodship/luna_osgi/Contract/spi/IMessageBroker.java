

package org.woodship.luna_osgi.Contract.spi;

import java.util.Map;

/**
 *
 * @author Killko Hon
 */
public interface IMessageBroker {
 public Map<String,Object> exec(String cmd,Map<String,Object> param);
}
