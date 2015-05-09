#!/bin/bash
jackd -dalsa &
sleep 2s
chuck --loop &
