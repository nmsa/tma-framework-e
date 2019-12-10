FROM    python:3.7

RUN     pip install virtualenv
RUN     pip install flask
RUN     pip install tmalibrary
RUN     pip install requests
RUN     pip install pycrypto

EXPOSE  8080

COPY    . /actuator_clouadea
WORKDIR /actuator_clouadea

CMD     ["python3", "cloudea-actuator.py"]