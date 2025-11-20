package com.stepin.jobopening.client;

import com.stepin.jobopening.dto.EligibilityResponse;
import com.stepin.jobopening.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Component
public class CampaignServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(CampaignServiceClient.class);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final WebClient webClient;

    public CampaignServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public EligibilityResponse checkEligibility(UUID campaignId, UUID companyId) {
        String uri = String.format("/campaigns/%s/companies/%s/eligibility", campaignId, companyId);

        logger.debug("Checking eligibility for campaignId={}, companyId={}", campaignId, companyId);

        try {
            EligibilityResponse response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(EligibilityResponse.class)
                    .timeout(REQUEST_TIMEOUT)
                    .block();

            logger.debug("Eligibility check result: canMutateJobs={}, reason={}",
                    response.isCanMutateJobs(), response.getReason());

            return response;

        } catch (WebClientResponseException.NotFound e) {
            logger.error("Campaign or company not found: campaignId={}, companyId={}", campaignId, companyId);
            throw new BusinessException("Campaign not found or company not invited");

        } catch (WebClientResponseException e) {
            logger.error("HTTP error from Campaign Service: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException("Failed to check eligibility: " + e.getMessage());

        } catch (Exception e) {
            logger.error("Error calling Campaign Service: {}", e.getMessage(), e);
            throw new BusinessException("Failed to communicate with Campaign Service: " + e.getMessage());
        }
    }
}
