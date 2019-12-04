FROM    python:3.7-alpine

RUN     pip install flask
RUN     pip install tmalibrary
RUN     pip install crypto

EXPOSE  8080

COPY    . /actuator_clouadea
WORKDIR /actuator_clouadea

CMD     ["python3", "cloudea-actuator.py"]