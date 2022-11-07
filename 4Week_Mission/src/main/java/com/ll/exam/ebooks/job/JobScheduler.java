package com.ll.exam.ebooks.job;

import com.ll.exam.ebooks.job.makeRebateOrderItem.MakeRebateOrderItemJobConfig;
import com.ll.exam.ebooks.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobScheduler {
    private final JobLauncher jobLauncher;

    private final MakeRebateOrderItemJobConfig makeRebateOrderItemJobConfig;
    private final Step makeRebateOrderItemStep1;

    @Scheduled(cron = "0 0 4 15 * *")
    public void runJob() {
        log.info(String.valueOf(LocalDateTime.now()));

        // job parameter 설정
        Map<String, JobParameter> confMap = new HashMap<>();
        String timeStr = Ut.date.format(LocalDateTime.now());
        confMap.put("time", new JobParameter(timeStr));
        JobParameters jobParameters = new JobParameters(confMap);

        try {
            jobLauncher.run(makeRebateOrderItemJobConfig.makeRebateOrderItemJob(makeRebateOrderItemStep1), jobParameters);
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
