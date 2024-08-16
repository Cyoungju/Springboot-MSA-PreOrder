package com.example.orderservice.order.service;

import com.example.orderservice.order.entity.Orders;
import com.example.orderservice.order.entity.OrdersItem;
import com.example.orderservice.order.entity.OrdersStatus;
import com.example.orderservice.order.repository.OrdersRepository;
import com.example.productservice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class OrderTasklet implements Tasklet {

    private final OrdersRepository ordersRepository;
    // TODO: Product API 요청
    //private final ProductRepository productRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("=====주문 상태변경 시작======");

        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간보다 생성 시간이 이전인 주문들 조회
        List<Orders> orderList = ordersRepository.findByCreateAtLessThanEqual(LocalDateTime.now());// 현재시간 전에 작성된 orders를 가지고 오기

        if(orderList == null || orderList.isEmpty()) {
            log.info("=====변경할 주문 상태가 없음.=====");
        } else {
            for (Orders order : orderList) {
                LocalDateTime createdAt = order.getCreateAt(); // 첫 주문 시간
                LocalDateTime modifyAt = order.getModifyAt(); // 수정된 시간
                Duration duration = Duration.between(createdAt, now);
                Duration duration2 = Duration.between(modifyAt, now);
                long daysElapsed = duration.toDays();
                long daysElapsed2 = duration2.toDays();

                long www = duration2.toMillis();


                // 상태에 따라 업데이트
                if(daysElapsed >= 1 && order.getOrderStatus().equals(OrdersStatus.ACCEPTED)){
                    order.changeOrderStatus(OrdersStatus.ON_DELIVERY);
                    log.info("1일 경과 ACCEPTED(결제완료) -> ON_DELIVERY(배달중) 상태 변경");
                }else if(daysElapsed >= 2 && order.getOrderStatus().equals(OrdersStatus.ON_DELIVERY)){
                    order.changeOrderStatus(OrdersStatus.SHIPPED);
                    log.info("2일 경과 ON_DELIVERY(배달중) -> SHIPPED(배달완료) 상태 변경");
                }else if(daysElapsed >= 3 && order.getOrderStatus().equals(OrdersStatus.SHIPPED)){
                    order.changeOrderStatus(OrdersStatus.CONFIRMED);
                    log.info("3일 경과 SHIPPED(배달완료) -> CONFIRMED(확정) 상태 변경");
                }

                if (www >= 1 && order.getOrderStatus().equals(OrdersStatus.RETURN_REQUESTED)) {
                    order.changeOrderStatus(OrdersStatus.RETURNED);
                    log.info("수정한 날로 1일경과 && RETURN_REQUESTED(반품진행중) -> RETURNED(반품 완료)");

                    // 상품 수량 복구
                    // TODO: 수량 복구 로직 수정
                    /*
                    List<OrdersItem> orderItems = order.getOrdersItems();
                    for(OrdersItem item : orderItems){
                        item.reQuantity();
                    }

                    productRepository.saveAll(orderItems.stream()
                            .map(OrdersItem::getProduct)
                            .collect(Collectors.toSet()));

                     */
                }

                // 상태 변경된 주문 저장
                ordersRepository.save(order);
            }
            log.info("===== 주문 상태 변경 완료 =====");
        }
        return RepeatStatus.FINISHED;
    }


}
