/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.woodship.luna_osgi.Application.UICmdBroker;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.woodship.luna_osgi.Contract.spi.IMessageBroker;

/**
 *
 * @author Killko Hon
 */
public class Broker implements IMessageBroker {
private CamelContext context;

    public CamelContext getContext() {
        return context;
    }

    public void setContext(CamelContext context) {
        this.context = context;
    }
    @Override
    public Map<String, Object> exec(String cmd, Map<String, Object> param) {
        try {
            //将命令发送到vm消息队列，队列名按UICMD_开头来命名,应用层处理该消息就可以了
            String CmdQueue = "vm:UICMD_" + cmd;
            ProducerTemplate template = context.createProducerTemplate();
            Exchange exchange = context.getEndpoint(CmdQueue).createExchange();
            exchange.setPattern(ExchangePattern.InOptionalOut);
            //将参数放到in消息体中
            if (param == null) {
                param = new HashMap<String, Object>();
            }
            exchange.getIn().setBody(param);
            //同步方式发送消息
            template.send(CmdQueue, exchange);
            //从out消息体取出返回信息
            if (exchange.getOut() != null) {
                Map<String, Object> ret = (Map<String, Object>) exchange.getOut().getBody();
                return ret;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
