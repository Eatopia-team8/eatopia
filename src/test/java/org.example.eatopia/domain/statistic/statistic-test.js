import http from 'k6/http';
import {check, group, sleep} from 'k6';
import {Trend} from 'k6/metrics';

// --------------------------------------------------------------------------------
// 🚨 Max 지연 요청을 포착하기 위한 임계값 (200ms)
// --------------------------------------------------------------------------------
const MAX_LATENCY_THRESHOLD = 200; // 밀리초(ms)

// --------------------------------------------------------------------------------
// 헬퍼 함수
// --------------------------------------------------------------------------------

// min, max 사이의 정수를 랜덤하게 반환 (e.g., 1, 5 -> 1,2,3,4,5 중 하나)
function randomIntBetween(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

// 배열(array)에서 랜덤하게 아이템 1개를 반환
function randomItem(array) {
    return array[Math.floor(Math.random() * array.length)];
}

// --------------------------------------------------------------------------------
// 환경 변수 (사용자 환경변수를 우선 사용)
// --------------------------------------------------------------------------------
const BASE_URL = __ENV.BASE_URL || "http://host.docker.internal:8080/api/eatopia";
const ADMIN_TOKEN = __ENV.ADMIN_TOKEN || "토큰";
const SELLER_TOKEN = __ENV.SELLER_TOKEN || "토큰";
// Java 코드에서 5명의 판매자(sellerCount = 5)를 생성했으므로, 1~5 사이의 ID를 사용합니다.
const SELLER_IDS = [1, 2, 3, 4, 5];

// --------------------------------------------------------------------------------
// 메트릭
// --------------------------------------------------------------------------------
const sellerSalesTrend = new Trend('seller_sales_duration');
const summarySalesTrend = new Trend('summary_sales_duration');
const adminSellerSalesTrend = new Trend('admin_seller_sales_duration');

// --------------------------------------------------------------------------------
// 부하 테스트 옵션 (1000 VUs)
// --------------------------------------------------------------------------------
export const options = {
    scenarios: {
        seller_checks_own_sales: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                {duration: '30s', target: 800}, // 800 VUs (80%)
                {duration: '1m', target: 800},
                {duration: '10s', target: 0},
            ],
            exec: 'sellerScenario',
            gracefulRampDown: '10s',
        },
        admin_checks_summary_and_seller: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                {duration: '30s', target: 200}, // 200 VUs (20%)
                {duration: '1m', target: 200},
                {duration: '10s', target: 0},
            ],
            exec: 'adminScenario',
            gracefulRampDown: '10s',
        },
    },
    thresholds: {
        'http_req_duration': ['p(95)<1000'], // 95%의 요청이 1초(1000ms) 안에 완료
        'http_req_failed': ['rate<0.01'],    // 실패율 1% 미만
    },
};

// --------------------------------------------------------------------------------
// 랜덤 날짜 생성 헬퍼 함수
// --------------------------------------------------------------------------------
function getRandomDateRange() {
    // Java 코드가 365일간 데이터를 분산시켰으므로,
    // 현재 연도(2025)와 작년 연도(2024)를 모두 쿼리합니다.
    const year = randomItem([2024, 2025]);

    const startMonth = randomIntBetween(1, 12);
    const startDay = randomIntBetween(1, 28);
    // 종료일이 시작일보다 빠르지 않도록 보정
    const endMonth = randomIntBetween(startMonth, 12);
    const endDay = randomIntBetween((startMonth === endMonth) ? startDay : 1, 28);

    const startDate = `${year}-${String(startMonth).padStart(2, '0')}-${String(startDay).padStart(2, '0')}`;
    const endDate = `${year}-${String(endMonth).padStart(2, '0')}-${String(endDay).padStart(2, '0')}`;

    return {startDate, endDate};
}


// --------------------------------------------------------------------------------
// 시나리오 1: SELLER (랜덤 파라미터 적용)
// --------------------------------------------------------------------------------
export function sellerScenario() {
    const sellerParams = {headers: {'Authorization': `Bearer ${SELLER_TOKEN}`}};

    const {startDate, endDate} = getRandomDateRange();
    const period = randomItem(['daily', 'monthly']);

    group('SELLER: 자신의 매출 조회', () => {
        const url = `${BASE_URL}/v2/statistic/seller?period=${period}&startDate=${startDate}&endDate=${endDate}`;
        const res = http.get(url, sellerParams);
        const duration = res.timings.duration;

        check(res, {'SELLER 랜덤 조회 (status 200)': (r) => r.status === 200});

        // ❗ [지연 로그] 200ms 초과 시 경고 로그 출력
        if (duration > MAX_LATENCY_THRESHOLD) {
            console.warn(`🔥 SLOW REQUEST (SELLER): ${duration.toFixed(2)}ms | URL: ${url}`);
        }

        sellerSalesTrend.add(duration);
    });

    sleep(Math.random() * 3 + 2); // 2~5초 대기
}

// --------------------------------------------------------------------------------
// 시나리오 2: ADMIN (랜덤 파라미터 적용)
// --------------------------------------------------------------------------------
export function adminScenario() {
    const adminParams = {headers: {'Authorization': `Bearer ${ADMIN_TOKEN}`}};

    const {startDate, endDate} = getRandomDateRange();
    const period = randomItem(['daily', 'monthly']);
    const randomSellerId = randomItem(SELLER_IDS); // 1~5 중 랜덤 선택

    group('ADMIN: 전체 매출 요약 조회', () => {
        const summaryUrl = `${BASE_URL}/v2/statistic/summary?period=${period}&startDate=${startDate}&endDate=${endDate}`;
        const summaryRes = http.get(summaryUrl, adminParams);
        const duration = summaryRes.timings.duration;

        check(summaryRes, {'ADMIN 요약 랜덤 (status 200)': (r) => r.status === 200});

        // ❗ [지연 로그] 200ms 초과 시 경고 로그 출력
        if (duration > MAX_LATENCY_THRESHOLD) {
            console.warn(`🔥 SLOW REQUEST (ADMIN SUMMARY): ${duration.toFixed(2)}ms | URL: ${summaryUrl}`);
        }

        summarySalesTrend.add(duration);
    });

    sleep(Math.random() * 2 + 1);

    group('ADMIN: 특정 판매자 매출 조회', () => {
        const sellerUrl = `${BASE_URL}/v2/statistic/seller?sellerId=${randomSellerId}&period=${period}&startDate=${endDate}&endDate=${endDate}`;
        const sellerDailyRes = http.get(sellerUrl, adminParams);
        const duration = sellerDailyRes.timings.duration;

        check(sellerDailyRes, {'ADMIN 판매자 랜덤 조회 (status 200)': (r) => r.status === 200});

        // ❗ [지연 로그] 200ms 초과 시 경고 로그 출력
        if (duration > MAX_LATENCY_THRESHOLD) {
            console.warn(`🔥 SLOW REQUEST (ADMIN SELLER): ${duration.toFixed(2)}ms | URL: ${sellerUrl}`);
        }

        adminSellerSalesTrend.add(duration);
    });

    sleep(Math.random() * 3 + 2);
}