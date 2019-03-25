package com.velocitos.webhook.controller;

import com.velocitos.webhook.util.HFUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class WebHookAPIController {


    private static final Logger logger = LoggerFactory.getLogger(WebHookAPIController.class);
    private static final String SECRET_TOKEN = "===SECRET TOKEN FROM ADMIN PORTAL===";


    /**
     * API to verify the webHook configuration, token secret is received as header "X-healthfeed-signature" and it is encoded
     * and return back in the response.
     *
     * @param allRequestParams
     * @param event
     * @param signature
     * @param delivery
     * @return
     */

    @RequestMapping(value = "/api/v1/analytics", method = {RequestMethod.GET})
    @ResponseBody
    public JSONObject getverify(@RequestParam(required = false) Map<String, String> allRequestParams,
                                @RequestHeader(value = "X-healthfeed-trigger", required = false) final String event,
                                @RequestHeader(value = "X-healthfeed-signature", required = false) final String signature,
                                @RequestHeader(value = "X-healthfeed-delivery", required = false) final String delivery) {

        if (SECRET_TOKEN.equals(signature)) {
            // valid api call
        } else {
            // invalid api call
            // corrective action for invalid call here
        }

        String msg = allRequestParams.get("data");
        String mac = null;
        try {
            mac = HFUtils.encode(signature, msg);
        } catch (Exception e) {
            logger.error("Error:{}", e);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("secret_token", mac);

        return jsonObject;
    }

    /**
     * Save the analytics in the static in-memory object.
     *
     * @param httpEntity is for getting request body.
     */

    @RequestMapping(value = "/api/v1/analytics", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public void saveAnalytics(HttpEntity<String> httpEntity,
                              @RequestHeader(value = "X-healthfeed-trigger", required = false) final String event,
                              @RequestHeader(value = "X-healthfeed-signature", required = false) final String signature,
                              @RequestHeader(value = "X-healthfeed-delivery", required = false) final String delivery) {
        if (SECRET_TOKEN.equals(signature)) {
            // valid api call
        } else {
            // invalid api call
            // corrective action for invalid call here
        }
        final String json = httpEntity.getBody();
        addJSONStringForUI(json);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //=====================================================================================================================================
    // FOLLOWING CODE IS ONLY USED BY UI IN THIS SAMPLE IMPLEMENTATION OF HEALTHFEED API WEBHOOK
    // IT IS NOT NEEDED FOR PRODUCTION
    // FOLLOWING SHOULD BE REMOVED FROM PRODUCTION IMPLEMENTATION
    //=====================================================================================================================================
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Responses List is used for UI only This should be removed from production implementation
     */
    private static List<String> responses = Collections.synchronizedList(new ArrayList<>());

    /**
     * Render the analytics page to show the live analytics
     *
     * @return analytics stored for the session.
     */
    @RequestMapping(value = {"/analytics/page", "/"}, method = RequestMethod.GET)
    public String getCampaignAnalyticsPage() {
        return "analytics";
    }

    /**
     * Returns the responses stored in the in-memory object for the session.
     *
     * @return <response>
     */
    @RequestMapping(value = "/api/v1/analytics/responses", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getCampaignAnalytics() {
        return responses;
    }

    /**
     * Delete the analytics data stored in-memory object to start storing the new analytics data.
     */

    @RequestMapping(value = "/api/v1/analytics/clear", method = RequestMethod.GET)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void clearData() {
        responses.clear();
    }

    private void addJSONStringForUI(final String json) {
        responses.add(json);
    }


}
