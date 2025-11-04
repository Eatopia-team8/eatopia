import http from 'k6/http';
import {check, group, sleep} from 'k6';
import {Trend} from 'k6/metrics';

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
// 환경 변수
// --------------------------------------------------------------------------------
const BASE_URL = __ENV.BASE_URL || "http://host.docker.internal:8080/api/eatopia";
const ADMIN_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3IiwiYXV0aCI6IlJPTEVfQURNSU4iLCJlbWFpbCI6InF3ZXJAbmF2ZXIuY29tIiwibmFtZSI6Iuq0gOumrOyekCIsImV4cCI6MTc2MjI1NTM0Mn0.rsdaGviEIGRyg2DBlF82ILzSb9ETNkKTzlOAlGap-f3ssXWziNxSbIz2cu9hY0TAyKqqf5iioidc3hWfuVfFyg";
const SELLER_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4IiwiYXV0aCI6IlJPTEVfU0VMTEVSIiwiZW1haWwiOiJxd2VydEBuYXZlci5jb20iLCJuYW1lIjoi7YyQ66ek7J6QIiwiZXhwIjoxNzYyMjU1MzU4fQ.YcZ9eZ3pozH97rhMOQLtai0zGJO2FhQtv1RCJWaParTKccc1R79iXjZqyPEW2gysngwFagFTsD3cQo1sbJRAOg";
// Java 코드에서 5명의 판매자(sellerCount = 5)를 생성했으므로, 1~5 사이의 ID를 사용합니다.
const SELLER_IDS = [1, 2, 3, 4, 5];

// --------------------------------------------------------------------------------
// ⚠️ [디버깅 로그 추가] ⚠️
// k6가 실제로 읽고 있는 토큰 값을 출력합니다.
// --------------------------------------------------------------------------------
console.log(`[DEBUG] k6가 읽은 ADMIN_TOKEN (앞 15자리): ${ADMIN_TOKEN ? ADMIN_TOKEN.substring(0, 15) : 'undefined'}...`);
console.log(`[DEBUG] k6가 읽은 SELLER_TOKEN (앞 15자리): ${SELLER_TOKEN ? SELLER_TOKEN.substring(0, 15) : 'undefined'}...`);
// --------------------------------------------------------------------------------


// --------------------------------------------------------------------------------
// 메트릭
// --------------------------------------------------------------------------------
const sellerSalesTrend = new Trend('seller_sales_duration');
const summarySalesTrend = new Trend('summary_sales_duration');
const adminSellerSalesTrend = new Trend('admin_seller_sales_duration');

// --------------------------------------------------------------------------------
// 부하 테스트 옵션 (200 VUs)
// --------------------------------------------------------------------------------
export const options = {
    scenarios: {
        seller_checks_own_sales: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                {duration: '30s', target: 160}, // 160 VUs (80%)
                {duration: '1m', target: 160},
                {duration: '10s', target: 0},
            ],
            exec: 'sellerScenario',
            gracefulRampDown: '10s',
        },
        admin_checks_summary_and_seller: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                {duration: '30s', target: 40}, // 40 VUs (20%)
                {duration: '1m', target: 40},
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
        const res = http.get(
            // API 경로: /v2/statistic/seller
            `${BASE_URL}/v2/statistic/seller?period=${period}&startDate=${startDate}&endDate=${endDate}`,
            sellerParams
        );

        check(res, {'SELLER 랜덤 조회 (status 200)': (r) => r.status === 200});

        if (res.status !== 200) {
            console.error(`❌ Failed Seller Request: ${res.status} - ${res.body}`);
        }

        sellerSalesTrend.add(res.timings.duration);
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
        const summaryRes = http.get(
            // API 경로: /v2/statistic/summary
            `${BASE_URL}/v2/statistic/summary?period=${period}&startDate=${startDate}&endDate=${endDate}`,
            adminParams
        );
        check(summaryRes, {'ADMIN 요약 랜덤 (status 200)': (r) => r.status === 200});
        summarySalesTrend.add(summaryRes.timings.duration);
    });

    sleep(Math.random() * 2 + 1);

    group('ADMIN: 특정 판매자 매출 조회', () => {
        const sellerDailyRes = http.get(
            // API 경로: /v2/statistic/seller
            `${BASE_URL}/v2/statistic/seller?sellerId=${randomSellerId}&period=${period}&startDate=${startDate}&endDate=${endDate}`,
            adminParams
        );
        check(sellerDailyRes, {'ADMIN 판매자 랜덤 조회 (status 200)': (r) => r.status === 200});
        adminSellerSalesTrend.add(sellerDailyRes.timings.duration);
    });

    sleep(Math.random() * 3 + 2);
}