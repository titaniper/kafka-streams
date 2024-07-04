import axios from 'axios';

// Axios 인스턴스 생성
const instance = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
});

// 난수 생성 함수
const getRandomNumber = (): number => {
  return Math.floor(Math.random() * 1000);
};

// POST 요청 함수
const postRequest = async (n: string) => {
  const body = {
      name: `partitioned.ben.ere-${n}`,
  };

  try {
      const response = await instance.post('/topics', body);
      console.log(`Response for ${body.name}: `, response.data);
  } catch (error) {
      console.error(`Error for ${body.name}: `, error);
  }
};


// 100번의 POST 요청 보내기
const sendRequests = async () => {
  await postRequest("debezium.ben.ddd_event")
  for (let i = 0; i < 10; i++) {
      const randomNumber = getRandomNumber();
      await postRequest(String(randomNumber));
  }
};

sendRequests();