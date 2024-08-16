package com.example.ecommerceproject.order.config;


import com.example.ecommerceproject.order.repository.OrdersRepository;
import com.example.ecommerceproject.order.service.OrderTasklet;
import com.example.ecommerceproject.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final OrdersRepository ordersRepository;

    private final ProductRepository productRepository;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;


    @Bean
    public Job orderJob(){
        return new JobBuilder("orderJob", jobRepository) // 메서드의 이름과 동일하게
                .start(changeOrderStatus()) // 실행할 step
                .on("FAILED")// 실행할 step이 실패 할 경우
                .stopAndRestart(changeOrderStatus()) // 멈추거나 해당 Step을 재실행
                .on("*")// 실패 외의 경우
                .end() // 해당 Step 종료
                .end() // 모든 작업 종료
                .build();
    }


    @Bean
    public Step changeOrderStatus() {
        StepBuilder stepBuilder = new StepBuilder("changeOrderStatus", jobRepository); // 메소드 이름과 동일학세

        Tasklet tasklet = new OrderTasklet(ordersRepository, productRepository);

        return stepBuilder.tasklet(tasklet, transactionManager).build();// 처리할 Tasklet을 생성

    }

}

