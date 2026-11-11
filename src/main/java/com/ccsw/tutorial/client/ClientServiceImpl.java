package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccsw
 *
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Client> findAll() {

        return (List<Client>) this.clientRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public void save(Long id, ClientDto dto) {

        Client existingClient = clientRepository.findByName(dto.getName());

        if (id == null) {

            if (existingClient != null) {
                throw new ClientException("Ya existe un cliente con ese nombre");
            }

            Client client = new Client();
            client.setName(dto.getName());
            clientRepository.save(client);

        } else {

            Client client = clientRepository.findById(id).orElseThrow();

            if (existingClient != null && !existingClient.getId().equals(client.getId())) {

                throw new ClientException("Ya existe un cliente con ese nombre");
            }

            client.setName(dto.getName());
            clientRepository.save(client);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {

        if (this.clientRepository.findById(id).orElse(null) == null) {
            throw new Exception("Not exists");
        }

        this.clientRepository.deleteById(id);
    }

}
