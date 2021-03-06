package sync.handler;

import elasticsearch.esapi.DocumentService;
import elasticsearch.esapi.HotDocumentService;
import org.springframework.context.ConfigurableApplicationContext;
import springcommon.ApplicationContextHolder;
import sync.common.ESHandleMessage;
import sync.handler.impl.ESProductListSyncHandler;
import sync.handler.impl.ESProductSyncHandler;
import sync.handler.impl.ESSKUSyncHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by I311352 on 10/17/2016.
 */

public class ESHandlerManager {
    private Map<String, ESHandler> handlerMap = new HashMap<String, ESHandler>();

    public void initHandler(ConfigurableApplicationContext context) {
        //DocumentService service = ApplicationContextHolder.getBean("documentService", DocumentService.class);

        ESProductSyncHandler productSyncHandler = new ESProductSyncHandler();
        productSyncHandler.setDocumentService(context.getBean("documentService", DocumentService.class));
        productSyncHandler.setHotDocumentService(context.getBean("hotDocumentService", HotDocumentService.class));
        handlerMap.put("Product.CREATE", productSyncHandler);
        handlerMap.put("Product.UPDATE", productSyncHandler);

        ESSKUSyncHandler esskuSyncHandler = new ESSKUSyncHandler();
        esskuSyncHandler.setDocumentService(context.getBean("documentService", DocumentService.class));
        esskuSyncHandler.setHotDocumentService(context.getBean("hotDocumentService", HotDocumentService.class));
        handlerMap.put("SKU.CREATE", esskuSyncHandler);
        handlerMap.put("SKU.UPDATE", esskuSyncHandler);

        ESProductListSyncHandler listSyncHandler = new ESProductListSyncHandler();
        listSyncHandler.setDocumentService(context.getBean("documentService", DocumentService.class));
        listSyncHandler.setHotDocumentService(context.getBean("hotDocumentService", HotDocumentService.class));
        handlerMap.put("CatalogSKU.LIST", listSyncHandler);
        handlerMap.put("CatalogSKU.UNLIST", listSyncHandler);
    }

    public List<String> getRoutingKeyList() {
        List<String> routingKeyList = handlerMap.entrySet().stream().map(val -> val.getKey())
                .collect(Collectors.toList());
        return routingKeyList;
    }

    public ESHandler getHandler(ESHandleMessage message) {
        String key = message.getType() + "." + message.getAction();
        return handlerMap.get(key);
    }

    public Map<String, ESHandler> getHandlerMap() {
        return handlerMap;
    }

    public void setHandlerMap(Map<String, ESHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }
}
