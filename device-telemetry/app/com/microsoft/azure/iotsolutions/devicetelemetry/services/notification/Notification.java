package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation.LogicApp;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models.ActionAsaModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.libs.ws.WSClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class Notification {
    private INotification implementation;
    private List<ActionAsaModel> actionList;
    private IServicesConfig servicesConfig;
    private String ruleId;
    private String ruleName;
    private String ruleDescription;
    private WSClient wsClient;

    @Inject
    public Notification(WSClient wsClient, IServicesConfig servicesConfig) {
        this.wsClient = wsClient;
        this.servicesConfig = servicesConfig;
    }

    public void setAlarmInformation(String ruleId, String ruleDescription) {
        this.ruleId = ruleId;
        this.ruleDescription = ruleDescription;
    }

    public List<ActionAsaModel> getActionList() {
        return actionList;
    }

    public void setActionList(List<ActionAsaModel> actionList) {
        this.actionList = actionList;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public CompletionStage executeAsync() {
        try {
            for(ActionAsaModel action : this.actionList){
                switch (action.getActionType()){
                    case "Email":
                        implementation = new LogicApp(wsClient);
                        Map<String, String> credentialMap = new HashMap<String, String>();
                        // credentialMap.put("endPointURL", "https://prod-00.southeastasia.logic.azure.com:443/workflows/1f2493004aea43e1ac661f071a15f330/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=DIfPL17M7qydXwHxD7g-_K-P3mE6dqYuv7aDfbQji94");
                        credentialMap.put("endPointURL", this.servicesConfig.getLogicAppEndPointUrl()); // figure out where this lies and replace
                        implementation.setCredentials(credentialMap);
                }
                implementation.setMessage((String) action.getParameters().get("Template"), this.ruleId, this.ruleDescription);
                implementation.setReceiver(((ArrayList<String>) action.getParameters().get("Email")).get(0));
                implementation.execute(); // how to make it await
            }
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }
}