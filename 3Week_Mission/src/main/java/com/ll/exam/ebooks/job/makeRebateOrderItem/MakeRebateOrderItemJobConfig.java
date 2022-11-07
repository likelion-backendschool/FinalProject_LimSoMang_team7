package com.ll.exam.ebooks.job.makeRebateOrderItem;

import com.ll.exam.ebooks.app.order.entity.OrderItem;
import com.ll.exam.ebooks.app.order.repository.OrderItemRepository;
import com.ll.exam.ebooks.app.rebate.entity.RebateOrderItem;
import com.ll.exam.ebooks.app.rebate.repository.RebateOrderItemRepository;
import com.ll.exam.ebooks.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class MakeRebateOrderItemJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final OrderItemRepository orderItemRepository;
    private final RebateOrderItemRepository rebateOrderItemRepository;

    @Bean
    public Job makeRebateOrderItemJob(Step makeRebateOrderItemStep1) {
        log.info("====================== Job 실행 ======================");
        return jobBuilderFactory.get("makeRebateOrderItemJob")
                .start(makeRebateOrderItemStep1)
                .build();
    }

    @Bean
    @JobScope
    public Step makeRebateOrderItemStep1(
            ItemReader orderItemReader,
            ItemProcessor orderItemToRebateOrderItemProcessor,
            ItemWriter rebateOrderItemWriter
    ) {
        log.info("====================== Step 실행 ======================");
        return stepBuilderFactory.get("makeRebateOrderItemStep1")
                .<OrderItem, RebateOrderItem>chunk(100)
                .reader(orderItemReader)
                .processor(orderItemToRebateOrderItemProcessor)
                .writer(rebateOrderItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<OrderItem> orderItemReader(
            @Value("#{jobParameters[time]}") String time
    ) {
        log.info("====================== ItemReader 실행 ======================");
        LocalDateTime createDate = Ut.date.parse(time);
        LocalDateTime targetDate = Ut.date.parse(time);
//        LocalDateTime targetDate = createDate.minusMonths(1);

        int year = targetDate.getYear();
        int month = targetDate.getMonthValue();
        int monthEndDay = Ut.date.getEndDayOf(year, month);
        String yearMonth = "%d-%d".formatted(year, month);

        LocalDateTime fromDate = Ut.date.parse(yearMonth + "-01 00:00:00.000000");
        LocalDateTime toDate = Ut.date.parse(yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay));

        log.info("fromDate: " + fromDate);
        log.info("toDate: " + toDate);

        return new RepositoryItemReaderBuilder<OrderItem>()
                .name("orderItemReader")
                .repository(orderItemRepository)
                .methodName("findAllByPayDateBetween")
                .pageSize(100)
                .arguments(Arrays.asList(fromDate, toDate))
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<OrderItem, RebateOrderItem> orderItemToRebateOrderItemProcessor() {
        log.info("====================== ItemProcessor 실행 ======================");
        return RebateOrderItem::new;
    }

    @Bean
    @StepScope
    public ItemWriter<RebateOrderItem> rebateOrderItemWriter() {
        log.info("====================== ItemWriter 실행 ======================");
        return items -> items.forEach(item -> {
            RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);

            if (oldRebateOrderItem != null) {
                rebateOrderItemRepository.delete(oldRebateOrderItem);
            }

            rebateOrderItemRepository.save(item);
        });
    }

}
