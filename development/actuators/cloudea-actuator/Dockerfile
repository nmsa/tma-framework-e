FROM    python:3.7-alpine

RUN     pip install tmalibrary
RUN     pip install crypto

COPY    . /actuator_clouadea
WORKDIR /actuator_clouadea

CMD     ["gunicorn", "-w 4", "main:app"]